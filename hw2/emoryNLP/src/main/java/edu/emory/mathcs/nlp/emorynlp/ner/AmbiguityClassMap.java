package edu.emory.mathcs.nlp.emorynlp.ner;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.ngram.Bigram;
import edu.emory.mathcs.nlp.common.collection.ngram.Unigram;
import edu.emory.mathcs.nlp.common.collection.tuple.ObjectDoublePair;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.ner.features.PrefixTree;

public class AmbiguityClassMap implements Serializable
{
	private static final long serialVersionUID = 3515412091681651812L;
	private Map<String,List<String>> ambiguity_class;
	private Bigram<String,String> pos_count;
//	private PrefixTree pos_count;
	
	public AmbiguityClassMap()
	{
		ambiguity_class = new HashMap<>();
		pos_count = new Bigram<>();
//		pos_count = new PrefixTree();
	}
	
//	public void add(NLPNode[] nodes)
//	{
//		for (NLPNode node : nodes) add(node);
//	}
	
	public void add(String word, String index)
	{
//		pos_count.add(word, String.valueOf(index));
		pos_count.add(word, index);

		
	}
	
	public void expand(double threshold)
	{
		List<ObjectDoublePair<String>> ngram;
		
		for (Entry<String,Unigram<String>> e : pos_count.entrySet())
		{
			
			ngram = e.getValue().toList(threshold);
			if (ngram.isEmpty()) continue;
			Collections.sort(ngram);
			
			List<String> ogram = ambiguity_class.get(e.getKey());
			
			if (ogram != null)
			{
				ngram.removeIf(u -> ogram.contains(u.o));
				ogram.addAll(ngram.stream().map(u -> u.o).collect(Collectors.toList()));
			}
			else
				ambiguity_class.put(e.getKey(), ngram.stream().map(u -> u.o).collect(Collectors.toList()));
		}
		
		pos_count = new Bigram<>();
//		pos_count = new PrefixTree();
	}
	
	/** @return the ambiguity class of the word-form. */
	public String get(NLPNode node)
	{
		List<String> ambi = ambiguity_class.get(toKey(node));
		return (ambi != null) ? Joiner.join(ambi, StringConst.UNDERSCORE) : null;
	}
	
	public int size()
	{
		return ambiguity_class.size();
	}
	
	private String toKey(NLPNode node)
	{
		return node.getSimplifiedWordForm();
	}
}
