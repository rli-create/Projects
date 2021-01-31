from datetime import datetime, timedelta
import os
from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.subdag_operator import SubDagOperator
from airflow.operators import (StageToRedshiftOperator, LoadFactOperator, DataQualityOperator)
from helpers import SqlQueries
from subdag import load_dimensions_subdag

# AWS_KEY = os.environ.get('AWS_KEY')
# AWS_SECRET = os.environ.get('AWS_SECRET')

start_date = datetime(2018, 11, 2)
end_date = datetime(2018, 11, 4)

default_args = {
    'owner': 'Ryan',
    'depends_on_past': False,
    'start_date': start_date,
    'end_date': end_date,
    'retries': 3,
    'retry_delay': timedelta(minutes=5),
    'catchup': False
}

dag = DAG('sparkify_dag',
          default_args=default_args,
          description='Load and transform data in Redshift with Airflow',
          schedule_interval='@hourly',
          max_active_runs=1
        )

dimensions_task_id = "dimensions_table_subdag"
dimensions_subdag_task = SubDagOperator(
    subdag=load_dimensions_subdag(
        parent_dag_name='sparkify_dag',
        task_id=dimensions_task_id,
        reshift_conn_id='redshift',
        start_date=start_date
    ),
    task_id=dimensions_task_id,
    dag=dag
)

start_operator = DummyOperator(task_id='Begin_execution',  dag=dag)

#create_tables = CreateTablesOperator(
#    task_id='Create_tables',
#    dag=dag,
#    redshift_conn_id="redshift"
#)

staging_events_to_redshift = StageToRedshiftOperator(
    task_id='Stage_events',
    dag=dag,
    redshift_conn_id="redshift",
    aws_credentials_id="aws_credentials",
    table="staging_events",
    s3_bucket="udacity-dend",
    s3_key="log_data/{{macros.ds_format(yesterday_ds, '%Y-%m-%d', '%Y')}}/{{macros.ds_format(yesterday_ds, '%Y-%m-%d', '%m')}}/{{yesterday_ds}}-events.json",
    json_format="s3://udacity-dend/log_json_path.json",
    region="us-west-2"
)

staging_songs_to_redshift = StageToRedshiftOperator(
    task_id='Stage_songs',
    dag=dag,
    redshift_conn_id="redshift",
    aws_credentials_id="aws_credentials",
    table="staging_songs",
    s3_bucket="udacity-dend",
    s3_key="song_data",
    json_format="auto ignorecase",
    region="us-west-2"
)


load_songplays_table = LoadFactOperator(
    task_id='Load_songplays_fact_table',
    dag=dag,
    redshift_conn_id="redshift",
    table="songplays",
    sql_query=SqlQueries.songplays_table_query,
    append_data=True
)

run_quality_checks = DataQualityOperator(
    task_id='Run_data_quality_checks',
    dag=dag,
    redshift_conn_id="redshift",
    nonempty_tables_to_check=["songplays", "songs", "artists", "users", "time"],
    sql_stats_check=[(SqlQueries.count_of_nulls_in_songs_table, 0),
                     (SqlQueries.count_of_nulls_in_artists_table, 0),
                     (SqlQueries.count_of_nulls_in_users_table, 0),
                     (SqlQueries.count_of_nulls_in_songplays_table,0)]
)

end_operator = DummyOperator(task_id='Stop_execution',  dag=dag)

start_operator >> [staging_events_to_redshift, staging_songs_to_redshift]
[staging_events_to_redshift, staging_songs_to_redshift] >> load_songplays_table
load_songplays_table >> dimensions_subdag_task
dimensions_subdag_task >> run_quality_checks
run_quality_checks >> end_operator