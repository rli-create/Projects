from airflow.hooks.postgres_hook import PostgresHook
from airflow.models import BaseOperator
from airflow.utils.decorators import apply_defaults

class DataQualityOperator(BaseOperator):

    ui_color = '#89DA59'
    
    count_sql = """
        SELECT COUNT(*) FROM {}
    """
    
    @apply_defaults
    def __init__(self,
                 # Define your operators params (with defaults) here
                 # Example:
                 redshift_conn_id = "",
                 nonempty_tables_to_check = [],
                 sql_stats_check = [],
                 *args, **kwargs):

        super(DataQualityOperator, self).__init__(*args, **kwargs)
        # Map params here
        # Example:
        self.redshift_conn_id = redshift_conn_id
        self.nonempty_tables_to_check = nonempty_tables_to_check
        self.sql_stats_check = sql_stats_check

    def execute(self, context):
        redshift = PostgresHook(postgres_conn_id=self.redshift_conn_id)
        
        #non empty check
        self.log.info("Starting non empty check on tables")
        for table in self.nonempty_tables_to_check:
            formatted_sql = DataQualityOperator.count_sql.format(table)
            records = redshift.get_records(formatted_sql)
            if len(records) < 1 or len(records[0]) < 1:
                raise ValueError(f"Non empty check failed. {table} returned no results")
            num_records = records[0][0]

            if num_records < 1:
                raise ValueError(f"Non empty check failed. {table} returned 0 records")
            self.log.info(f"Non empty check on table {table} check passed with {records[0][0]} records")
        
        #stats check
        self.log.info("Starting sql stats test")
        for test in self.sql_stats_check:
            query = test[0]
            expected = test[1]
            result = redshift.get_records(query)
            if result[0][0] != expected:
                raise ValueError(f"Stats check failed for query: {query}, expected result: {expected}, actual result: {result[0][0]}.")
                
            self.log.info(f"Stats check passed for query: {query}")