BEGIN	      {printing=0;}

/<cip>/	      { printing=1;}
/<\/td>/      { printing=0;}

	      { if(printing) print}
