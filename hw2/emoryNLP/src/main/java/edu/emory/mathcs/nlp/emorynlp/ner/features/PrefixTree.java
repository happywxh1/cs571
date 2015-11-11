package edu.emory.mathcs.nlp.emorynlp.ner.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PrefixTree {
//	private int count;
	private HashMap<String, Integer> countMap;
	private HashMap<Character, PrefixTree> prefixTree;
	
	public PrefixTree(){
		countMap = new HashMap<>();
		prefixTree = new HashMap<>();
	}
	
	public void add(String word, String index){
		PrefixTree currentTree = this;
		char[] cArray = word.toCharArray(); 
	
		int l = cArray.length;
		
		for (int i=0; i<l; i++){
			if (currentTree.getHashMap().containsKey(cArray[i])){
				if (i==l-1)
					currentTree.countAddOne(index);
				else{
					currentTree = currentTree.getHashMap().get(cArray[i]);
				}
			}
			else{
				PrefixTree newTree = new PrefixTree(); 
				currentTree.getHashMap().put(cArray[i], newTree);
				currentTree = newTree;
				if (i == l-1)
					newTree.countAddOne(index);
					return; 
			}
		}
	}
	
	public HashMap<String, Integer> getCountMap(){
		return countMap;
	}
	public HashMap<Character, PrefixTree> getHashMap(){
		return prefixTree;
	}
	public void countAddOne(String index){
		if (countMap.containsKey(index))
			countMap.put(index,countMap.get(index)+1);
		else
			countMap.put(index, 1);
	}
	
	
	

//	public static void main(String[] args) throws IOException{
//		PrefixTree prefixTree = new PrefixTree();
//		File file = new File("prefixData.txt");
//		
//		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
//		BufferedReader breader = new BufferedReader(reader);
//		String line; 
//		line = breader.readLine();
//		while (line != null){
//			String[] lineSplitted = line.split("\t");
//			if (lineSplitted.length>2){
//				System.out.println("more than one tab!"+line);
//			}
//			else{
//				String[] chunkSplitted = lineSplitted[0].split(" ");
//				String[] indexSplitted = lineSplitted[1].split(" ");
//				int[] indexs = new int[indexSplitted.length];
//				for (int j=0; j<indexSplitted.length; j++)
//					indexs[j] = Integer.valueOf(indexSplitted[j]);
//				
//				for (String word: chunkSplitted)
//					for (int index: indexs)
//						prefixTree.add(word, index);
//			}
//			line = breader.readLine();
//		}
//		
//		
//		
//		
//		breader.close();
//		reader.close();
//	}
}



//class PrefixTreeNode{
//	private int count;
//	private PrefixTree pTree; 
//	
//	public PrefixTreeNode(){
//		count = 0;
//		pTree = new PrefixTree();
//		
//	}
//	public int getCount(){
//		return count;
//	}
//	public PrefixTree getPrefixTree(){
//		return pTree;
//	}
//	public void countAddOne(){
//		count ++;
//	}
//}
