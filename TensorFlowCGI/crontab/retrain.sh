#!/bin/bash

NEW_NEG_FOLDER="/Users/nuaimat/Sites/www/uploads/negative"

for i in `ls -1 $NEW_NEG_FOLDER`;
do
   c=`ls -1 /Users/nuaimat/Sites/www/uploads/negative/$i | wc -l`
   if [ "$c" -ge 30 ]; then
    cp -rv /Users/nuaimat/Sites/www/uploads/negative/$i /Users/nuaimat/dataset/v1_jpg/sketches/;
    rm -rf /Users/nuaimat/Sites/www/uploads/negative/$i
   fi
done;

python retrain.py \
--bottleneck_dir=/Users/nuaimat/dataset/v1_jpg/bottlenecks \
--how_many_training_steps 500 \
--model_dir=/Users/nuaimat/dataset/v1_jpg/inception \
--output_graph=/Users/nuaimat/dataset/v1_jpg/retrained_graph.pb \
--output_labels=/Users/nuaimat/dataset/v1_jpg/retrained_labels.txt \
--image_dir /Users/nuaimat/dataset/v1_jpg/sketches \
--summaries_dir /Users/nuaimat/dataset/v1_jpg/retrain_logs

echo "Restarting Apache"
sudo apachectl restart
