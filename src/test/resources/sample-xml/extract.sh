for x in $(ls *.html); 
do 
	awk -f process.awk $x > ${x%.html}.xml;
done;

