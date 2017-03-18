#!/Users/nuaimat/anaconda3/bin/python3
# -*- coding: UTF-8 -*-
import tensorflow as tf, sys
import warnings
import json
import time
from flipflop import WSGIServer
from werkzeug.wrappers import Request, Response
import uuid
import tempfile


def store_file(request, fname):
    file = request.files.get('file')
    #fname = '/Users/nuaimat/Sites/www/'+ str(uuid.uuid4()) +'.jpg'

    if file:
        file.save(fname)
    return(fname)

def app(environ, start_response):
     request = Request(environ)
     #start_response('200 OK', [('Content-Type', 'application/json')])



       # Loads label file, strips off carriage return
     label_lines = [line.rstrip() for line
                      in tf.gfile.GFile("/Users/nuaimat/dataset/v1_jpg/retrained_labels.txt")]


     #print("Loading from src")
     # Unpersists graph from file
     with tf.gfile.FastGFile("/Users/nuaimat/dataset/v1_jpg/retrained_graph.pb", 'rb') as f:
       retrained_graph_lines = f.read();

     graph_def = tf.GraphDef()
     graph_def.ParseFromString(retrained_graph_lines)
     _ = tf.import_graph_def(graph_def, name='')

     fname = tempfile.NamedTemporaryFile(delete=True, suffix='.jpg')
     uploaded_image_path = store_file(request, fname.name);

     #image_path = "/Users/nuaimat/dataset/v1_jpg/test/car2.jpg"

     # Read in the image_data
     image_data = tf.gfile.FastGFile(uploaded_image_path, 'rb').read()

     retList = {}
     retList["data"] = [];
     retList["debug"] = [{'label': request.args.get('label', 'no value for label')}];
     retList["debug"].append({'file': uploaded_image_path});
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
             jScore = {}
             jScore['score'] = int(score*100)
             jScore['label'] = human_string
             retList["data"].append(jScore)

     response = Response(json.dumps(retList), mimetype='application/json')
     fname.close()
     return(response(environ, start_response))


WSGIServer(app).run()
