#/bin/sh

mkdir ~/.easy-web-shell
nohup java -jar ews.jar >$HOME/.easy-web-shell/log.txt 2>&1&
