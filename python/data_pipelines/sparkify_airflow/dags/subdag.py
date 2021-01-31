from airflow import DAG
from airflow.operators import LoadDimensionOperator
from helpers import SqlQueries

def load_dimensions_subdag(
        parent_dag_name,
        task_id,
        reshift_conn_id,
        *args, **kwargs):
    dag = DAG(
        f"{parent_dag_name}.{task_id}",
        **kwargs
    )
    
    load_users_dimension_table = LoadDimensionOperator(
        task_id='Load_users_dim_table',
        dag=dag,
        redshift_conn_id=reshift_conn_id,
        table="users",
        table_query=SqlQueries.users_table_query,
        append_data=True
    )
    
    load_songs_dimension_table = LoadDimensionOperator(
        task_id='Load_songs_dim_table',
        dag=dag,
        redshift_conn_id=reshift_conn_id,
        table="songs",
        table_query=SqlQueries.songs_table_query,
        append_data=True
    )
    
    load_artists_dimension_table = LoadDimensionOperator(
        task_id='Load_artists_dim_table',
        dag=dag,
        redshift_conn_id=reshift_conn_id,
        table="artists",
        table_query=SqlQueries.artists_table_query,
        append_data=True
    )
    
    load_time_dimension_table = LoadDimensionOperator(
        task_id='Load_time_dim_table',
        dag=dag,
        redshift_conn_id=reshift_conn_id,
        table="time",
        table_query=SqlQueries.time_table_query,
        append_data=True
    )
    
    return dag