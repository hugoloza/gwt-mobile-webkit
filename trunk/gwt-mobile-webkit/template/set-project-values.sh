#!/bin/bash

# Please set your values in this script before executing!!

TEMPLATE_TYPE_TITLE="Mobile WebKit"
TEMPLATE_TYPE_ID="mobile-webkit"
TEMPLATE_TITLE="Database"
TEMPLATE_ID="database"
TEMPLATE_CAPS="DATABASE"

TEMPFILE="/tmp/sed$PID.tmp"

function fdren {
  echo "Renaming files/dirs *$1* to *$2*..."
  for FILENAME in `find . -name *$1*`; do
    echo "$FILENAME" | sed -e "s/$1/$2/g" > $TEMPFILE
    NEWNAME=`cat $TEMPFILE`
    if [ -d $FILENAME ]; then
      echo "Renaming directory $FILENAME to $NEWNAME..."
      mkdir -p $NEWNAME
      mv $FILENAME/* $NEWNAME
      rm -Rf $FILENAME
    else
      echo "Renaming file $FILENAME to $NEWNAME..."
      mv $FILENAME $NEWNAME
    fi
  done
}

function runrenames {
  fdren templtype $TEMPLATE_TYPE_ID
  fdren TemplType $TEMPLATE_TYPE_TITLE
  fdren template $TEMPLATE_ID
  fdren Template $TEMPLATE_TITLE
  fdren TEMPLATE $TEMPLATE_CAPS
}

# run renames multiple times because of nested folder renames:
runrenames
runrenames

# rename in file content:
for FILENAME in `find . -name '*.xml' -type f` \
                `find . -name '*.properties' -type f` \
                `find . -name '*.conf' -type f` \
                `find . -name '*.txt' -type f` \
                `find . -name '*.css' -type f` \
                `find . -name '*.html' -type f` \
                `find . -name '*.java' -type f` \
                `find . -name '*.' -type f` \
                `find . -name '*.cmd' -type f` \
                `find . -name '*.bat' -type f` \
                `find . -name '*.sh' -type f`; do

  if [ $FILENAME != "./set-project-values.sh" ]; then
    sed -e "s/TemplType/$TEMPLATE_TYPE_TITLE/g" \
        -e "s/templtype/$TEMPLATE_TYPE_ID/g" \
        -e "s/Template/$TEMPLATE_TITLE/g" \
        -e "s/template/$TEMPLATE_ID/g" \
        -e "s/TEMPLATE/$TEMPLATE_CAPS/g" \
        $FILENAME > $TEMPFILE

    if [ -s $TEMPFILE ] && [ "`cat $TEMPFILE`" != "`cat $FILENAME`" ]; then
      echo "Finding and Replacing in file: $FILENAME..."
      mv $TEMPFILE $FILENAME
    fi
  fi
done

echo "Remove .svn directories? enter your root pwd!"
sudo find . -name .svn -exec rm -Rf {} ";"

# Include build-tools and eclipse/settings directories from GWT repository:
svn propset svn:externals "http://google-web-toolkit.googlecode.com/svn/trunk/build-tools build-tools" .
svn propset svn:externals "http://google-web-toolkit.googlecode.com/svn/trunk/eclipse/settings settings" eclipse

# Mark some scripts as svn:executable:
svn propset svn:executable ON samples/hello$TEMPLATE_ID/launch-scripts/linux/*
svn propset svn:executable ON samples/hello$TEMPLATE_ID/launch-scripts/mac/*

# removing this script:
rm -f set-project-values.sh
