PLAY_HOME=/home/play-1.2.5
PLAY=$PLAY_HOME/play
TSG_SRC=/home/tsg/tsg
TSG_PROD=/home/tsg/tsg_prod

cd $TSG_SRC
git pull origin master

cd $TSG_PROD
$PLAY stop $TSG_PROD

rm -Rf $TSG_PROD
mkdir $TSG_PROD
cp -R $TSG_SRC/* $TSG_PROD/
rm $TSG_PROD/README.md
rm $TSG_PROD/build.sh
cd $TSG_PROD
$PLAY deps --sync
$PLAY start
