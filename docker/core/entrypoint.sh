#!/bin/bash

cp /etc/apt/sources.list /etc/apt/sources.list.bak
cat > /etc/apt/sources.list <<EOF
# 默认注释了源码镜像以提高 apt update 速度，如有需要可自行取消注释
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic main restricted universe multiverse
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-updates main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-updates main restricted universe multiverse
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-backports main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-backports main restricted universe multiverse
deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-security main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-security main restricted universe multiverse

# 预发布软件源，不建议启用
# deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-proposed main restricted universe multiverse
# deb-src https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-proposed main restricted universe multiverse
EOF

apt-get update > /dev/null && apt-get install -y openjdk-8-jdk mysql-client-core-5.5 > /dev/null

while true
do
    echo "waiting for mvn package..."
	sleep 3
    if [ -f "/opt/core/core.jar" ] ; then
		echo "core.jar detected."
    	break
    fi
done    

HAS_MYSQL=1

while [ $HAS_MYSQL != 0 ]
do
	echo "waiting for hshe mysql server..."
	sleep 1
	mysqladmin --silent -h$DB_HOST -u$DB_USER -p$DB_PASS status
	HAS_MYSQL=$?
done

echo "hshe mysql server detected."

java -jar /opt/core/core.jar