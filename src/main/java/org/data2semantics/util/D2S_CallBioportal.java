package org.data2semantics.util;

import java.io.File;

public class D2S_CallBioportal {
	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println("Please provide the directory to read the snapshot of files to be annotated");
			return;
		}
		
		File snapshotDirectory = new File(args[0]);
		if(!snapshotDirectory.exists()){
			System.out.println("Pleas run first mvn -P create-snapshot, since there is no snapshot to be processed");
			return;
		}
		System.out.println("Here we will call bioportal");
		System.out.println(new File(args[0]).exists());
	}
}
