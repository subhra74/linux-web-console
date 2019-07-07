#/bin/sh

mkdir ~/.easy-cloud-shell

nohup java -jar cloud-shell.jar >$HOME/.easy-cloud-shell/log.txt 2>&1&


