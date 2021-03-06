package org.data2semantics.vocabulary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * The concept which has an URI
 * @author wibisono
 *
 */
public class D2S_Concept {
		String stringID;
		String mainTerm;
		
		//Removing duplicates
		Set<String> synonyms;
	
		/**
		 * @return the stringID
		 */
		public String getStringID() {
			return stringID;
		}
		/**
		 * @param stringID the stringID to set
		 */
		public void setStringID(String stringID) {
			this.stringID = stringID;
		}
		/**
		 * @return the mainTerm
		 */
		public String getMainTerm() {
			return mainTerm;
		}
		/**
		 * @param mainTerm the mainTerm to set
		 */
		public void setMainTerm(String mainTerm) {
			this.mainTerm = mainTerm;
		}
		/**
		 * @return the synonyms
		 */
		public Set<String> getSynonyms() {
			return synonyms;
		}
		/**
		 * @param synonyms the synonyms to set
		 */
		public void setSynonyms(Set<String> synonyms) {
			this.synonyms = synonyms;
		}
		
		public void addSynonym(String newSynonym) {
			if(synonyms==null){
				synonyms = new HashSet<String>();
			}
			synonyms.add(newSynonym);
		}
		@Override
		public String toString(){
			String result = "";
			result += mainTerm +"("+stringID+ ")\n";
			if(synonyms != null)
				for(String syn : synonyms){
					result += "   a.k.a "+syn+"\n";
				}
			return result;
		}
}
