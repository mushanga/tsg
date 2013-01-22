PLAY_HOME=/home/play-1.2.5
PLAY=$PLAY_HOME/play
TCOMMERCE_SRC=/home/tsg/tsg
TCOMMERCE_PROD=/home/tsg/tsg_prod

git pull

cd $TCOMMERCE_PROD
$PLAY stop $TCOMMERCE_PROD

rm -Rf $TCOMMERCE_PROD
mkdir $TCOMMERCE_PROD
cp -R $TCOMMERCE_SRC/* $TCOMMERCE_PROD/
rm $TCOMMERCE_PROD/README.md
rm $TCOMMERCE_PROD/build.sh
cd $TCOMMERCE_PROD
$PLAY deps --sync
$PLAY start -Dpidfile.path=$TCOMMERCE_PROD/server.pid --%prod
