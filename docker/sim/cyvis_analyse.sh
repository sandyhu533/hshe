#!/bin/bash
# EXE_PATH=/usr/local/cyvis-0.9/cyvis-0.9.jar
# EXE_PATH=/Users/sandyhu/Developer/hshe3/docker/sim/cyvis-0.9/cyvis-0.9.jar
EXE_PATH=$1
SUBS_PATH=$2
PROBLEM_ID=$3
SUBMISSION_ID1=$4
CLASS_FILE_PATH=$SUBS_PATH/$PROBLEM_ID/$SUBMISSION_ID1/Main.class
JAVA_FILE_PATH=$SUBS_PATH/$PROBLEM_ID/$SUBMISSION_ID1/Main.java
LINE_CT=`cat $JAVA_FILE_PATH | wc -l`;
echo $LINE_CT
COMMENT_LINE_CT=`cat $JAVA_FILE_PATH | grep "\/[\/\*]" | wc -l`;
echo $COMMENT_LINE_CT
LOGIC_LINE_CT=`cat $JAVA_FILE_PATH | grep -o ";" | wc -l`;
echo $LOGIC_LINE_CT
EMPTY_LINE_CT=`grep '^$' $JAVA_FILE_PATH | wc -l`
echo $EMPTY_LINE_CT
key_word_arr=("abstract" "assert" "boolean" "break" "byte" "case" "catch" "char" "class" "const"
"continue" "default" "do" "double" "else" "enum" "extends" "false" "final" "finally"
"float" "for" "goto" "if" "implements" "import" "instanceof" "int" "interface" "long"
"native" "new" "null" "package" "private" "protected" "public" "return" "short" "static"
"strictfp" "super" "switch" "synchronized" "this" "throw" "throws" "transient" "true" "try"
"void" "volatile" "while")
KEY_WORD_CT=0;
for((i=0; i<${#key_word_arr[*]}; i++))
do
key_word=${key_word_arr[i]}
TMP=`cat $JAVA_FILE_PATH | grep -o $key_word | wc -l`
# echo $key_word""$TMP
KEY_WORD_CT=`expr $TMP + $KEY_WORD_CT`
done
echo $KEY_WORD_CT
java -jar $EXE_PATH -p $CLASS_FILE_PATH -t out.txt
cat out.txt | sed 's/$/&,/g' | sed 's/,,/\\/g' | awk '
BEGIN{
RS="\\";
FS=",";
instruction_sum=0;
cc_sum=0;
method_sum=0;
}
{
# print "NR:"NR;
# print "NF:"NF;
# print "content:"$0;
if(NR+0>method_sum+0) method_sum=NR;
cc_sum+=$(NF);
# print "NF:"$(NF);
instruction_sum+=$(NF-1);
# print "NF-1:"$(NF-1);
}
END{
print method_sum;
print cc_sum;
print instruction_sum;
}
'
