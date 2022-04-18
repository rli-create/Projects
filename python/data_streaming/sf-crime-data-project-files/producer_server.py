from kafka import KafkaProducer
import json
import time


class ProducerServer(KafkaProducer):

    def __init__(self, input_file, topic, **kwargs):
        super().__init__(**kwargs)
        self.input_file = input_file
        self.topic = topic

    def generate_data(self):
        with open(self.input_file) as f:
            j = json.load(f)
            for row in reversed(j):
                message = self.dict_to_binary(row)
                # TODO send the correct data
                self.send(self.topic, message)
                print("sent message {}".format(message))
                time.sleep(1)
            f.close()

    def dict_to_binary(self, json_dict):
        return json.dumps(json_dict).encode('utf-8')
        