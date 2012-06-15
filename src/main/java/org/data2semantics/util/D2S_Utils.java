package org.data2semantics.util;

import java.io.File;
import java.util.HashMap;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class D2S_Utils {

		public static HashMap<String, String> loadSourceMap( String sourceFile){
			
			Repository repo = new SailRepository(new MemoryStore());
			HashMap<String, String> cacheNameToSourceMap = new HashMap<String, String>();
			
			try {
				repo.initialize();
				RepositoryConnection conn = repo.getConnection();
				
				conn.add(new File(sourceFile), "", RDFFormat.TURTLE);
				RepositoryResult<Statement> results = conn.getStatements(null, null, null, true);
				while(results.hasNext()){
					Statement curStatement = results.next();
					String origSource = curStatement.getSubject().toString();
					String localName = curStatement.getObject().toString().replaceAll("\"", "");
					cacheNameToSourceMap.put(localName,  origSource);
					

				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return cacheNameToSourceMap;
		}
}
