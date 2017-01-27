clear
echo "Installing Java"
sudo apt-get update
sudo apt-get install openjdk-7-jdk
sudo apt-get install ant
sudo apt-get install -y mongodb-org-server mongodb-org-shell mongodb-org-tools
sudo rm /var/lib/dpkg/lock
echo "Installing MongoDb"
sudo apt-get install mongodb
sudo service mongod start
sudo apt-get install mongodb-clients
echo "Install Redis"
sudo apt-get install build-essential -y
sudo apt-get install erlang-base-hipe -y
sudo apt-get install erlang-dev -y
sudo apt-get install erlang-manpages -y
sudo apt-get install erlang-eunit -y
sudo apt-get install erlang-nox -y
sudo apt-get install libicu-dev -y
sudo apt-get install libmozjs-dev -y
sudo apt-get install libcurl4-openssl-dev -y
sudo apt-get install libjna-java -y
sudo apt-get install -y python:software-properties
sudo add-apt-repository -y ppa:rwky/redis
sudo rm/var/cache/apt/archives/lock
sudo apt-get update
sudo apt-get install -y redis-server
sudo service redis-server stop
sudo service redis-server start


echo "Couch db"
sudo apt-get update
sudo apt-get install couchdb


exit 1
