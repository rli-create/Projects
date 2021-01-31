from airflow.hooks.postgres_hook import PostgresHook
from airflow.models import BaseOperator
from airflow.utils.decorators import apply_defaults

class LoadDimensionOperator(BaseOperator):

    ui_color = '#80BD9E'

    insert_sql = """
            INSERT INTO {}
            {}
    """
    
    @apply_defaults
    def __init__(self,
                 # Define your operators params (with defaults) here
                 redshift_conn_id="",
                 table="",
                 table_query="",
                 append_data=False,
                 *args, **kwargs):

        super(LoadDimensionOperator, self).__init__(*args, **kwargs)
        # Map params here
        self.redshift_conn_id = redshift_conn_id
        self.table = table
        self.table_query = table_query
        self.append_data = append_data

    def execute(self, context):
        redshift = PostgresHook(postgres_conn_id=self.redshift_conn_id)
        
        if not self.append_data:
            self.log.info('Deleting data from dimension table {}'.format(self.table))
            redshift.run('DELETE FROM {}'.format(self.table))
            
        self.log.info('Loading dimension table {}'.format(self.table))
        formatted_sql = LoadDimensionOperator.insert_sql.format(self.table, self.table_query)
        self.log.info('Executing query: {}'.format(formatted_sql))
        redshift.run(formatted_sql)
