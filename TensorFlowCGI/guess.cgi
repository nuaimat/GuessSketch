#!/Users/nuaimat/anaconda3/bin/python
import tensorflow as tf, sys
import warnings
import json


def json_serializer(key, value):
    if type(value) == str:
        return value, 1
    return json.dumps(value), 2

def json_deserializer(key, value, flags):
   if flags == 1:
       return value
   if flags == 2:
       return json.loads(value)
   raise Exception("Unknown serialization format")

warnings.filterwarnings("ignore")


from pymemcache.client.base import Client
client = Client(('localhost', 11211), serializer=json_serializer,
                deserializer=json_deserializer)
#client.set('some_key', 'some_value')


print("Content-Type: text/plain")
print()

image_path = "/Users/nuaimat/dataset/v1_jpg/test/car2.jpg"

# Read in the image_data
image_data = tf.gfile.FastGFile(image_path, 'rb').read()

label_lines = client.get(b'label_lines')
if not bool(label_lines):
  # Loads label file, strips off carriage return
  label_lines = [line.rstrip() for line
                     in tf.gfile.GFile("/Users/nuaimat/dataset/v1_jpg/retrained_labels.txt")]
  client.set('label_lines', label_lines) # might want to serialzie


print("before:")
print(dir())
# Unpersists graph from file
with tf.gfile.FastGFile("/Users/nuaimat/dataset/v1_jpg/retrained_graph.pb", 'rb') as f:
    graph_def = tf.GraphDef()
    graph_def.ParseFromString(f.read())
    _ = tf.import_graph_def(graph_def, name='')

print("after:")
print(dir())



'''
print("From memcache ")
print(client.get('some_key'))
'''

with tf.Session() as sess:
    # Feed the image_data as input to the graph and get first prediction
    softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')

    predictions = sess.run(softmax_tensor, \
             {'DecodeJpeg/contents:0': image_data})

    # Sort to show labels of first prediction in order of confidence
    top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]

    for node_id in top_k:
        human_string = label_lines[node_id]
        score = predictions[0][node_id]
        print('%s (score = %.5f)' % (human_string, score))
#print "Content-type: text/html\n\n";
#print "Hello, Turreta! How are you today?\n";
