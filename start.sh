pkill -f 'java -jar' || true
nohup java -jar ~/Chingu-Backend-0.0.1-SNAPSHOT.jar > ~/app.log 2>&1 &
