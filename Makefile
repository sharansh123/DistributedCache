build:
	mvn clean compile

run-leader: build
	mvn exec:java -Dexec.mainClass="my.cache.LeaderMain" -Dexec.args="4000 4000"

run-follower-1: build
	mvn exec:java -Dexec.mainClass="my.cache.FollowerMain" -Dexec.args="3000 4000"

run-follower-2: build
	mvn exec:java -Dexec.mainClass="my.cache.FollowerMain" -Dexec.args="2000 4000"

run-follower-3: build
	mvn exec:java -Dexec.mainClass="my.cache.FollowerMain" -Dexec.args="5000 4000"

run-client: build
	mvn exec:java -Dexec.mainClass="my.cache.ClientMain"