/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.train.NLPOnlineTrain;
import edu.emory.mathcs.nlp.emorynlp.ner.AmbiguityClassMap;
import edu.emory.mathcs.nlp.emorynlp.ner.NERConfig;
import edu.emory.mathcs.nlp.emorynlp.ner.NERState;
import edu.emory.mathcs.nlp.emorynlp.ner.NERTagger;
import edu.emory.mathcs.nlp.emorynlp.ner.features.NERFeatureTemplate0;
import it.unimi.dsi.fastutil.ints.IntIndirectHeaps;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTrain extends NLPOnlineTrain<NLPNode,NERState<NLPNode>>
{
	public NERTrain(String[] args)
	{
		super(args);
	}
	
	@Override
	protected NLPOnlineComponent<NLPNode,NERState<NLPNode>> createComponent(InputStream config)
	{
		return new NERTagger<>(config);
	}

	@Override
	protected void initComponent(NLPOnlineComponent<NLPNode,NERState<NLPNode>> component, List<String> inputFiles)
	{
		initComponentSingleModel(component, inputFiles);
		AmbiguityClassMap map = createAmbiguityClasseMap(component.getConfiguration(), inputFiles);
//		((NERTagger<NLPNode>)component).setAmbiguityClassMap(map);		
		((NERTagger<NLPNode>)component).setAmbiguityClassMap(map);		
//		component.getFeatureTemplate().getState().setAmbiguityClass(map);
//		((NERState<NLPNode>)component).setAmbiguityClass(map);		

	}
	
	@Override
	protected FeatureTemplate<NLPNode,NERState<NLPNode>> createFeatureTemplate()
	{
		switch (feature_template)
		{
		case  0: return new NERFeatureTemplate0<NLPNode>();
		default: throw new IllegalArgumentException("Unknown feature template: "+feature_template);
		}
	}
	
	@Override
	protected NLPNode createNode()
	{
		return new NLPNode();
	}
	
	static public void main(String[] args)
	{
		double t1=System.currentTimeMillis();
		new NERTrain(args).train();
		double t = System.currentTimeMillis() -t1;
		System.out.println("Total running time: "+t);
	}
	
//	protected AmbiguityClassMap createAmbiguityClasseMap(NLPConfig<NLPNode> configuration, List<String> inputFiles)
//	{
//		BinUtils.LOG.info("Collecting lexicons:\n");
//		AmbiguityClassMap ac = new AmbiguityClassMap();
//		NERConfig config = (NERConfig)configuration;
//		
//		iterate(configuration.getTSVReader(), inputFiles, nodes -> ac.add(nodes));
//		ac.expand(config.getAmbiguityClassThreshold());
//		
//		BinUtils.LOG.info(String.format("- # of ambiguity classes: %d\n", ac.size()));
//		return ac;
//	}	
	protected AmbiguityClassMap createAmbiguityClasseMap(NLPConfig<NLPNode> configuration, List<String> inputFiles)
	{
		BinUtils.LOG.info("Collecting lexicons:\n");
		AmbiguityClassMap ac = new AmbiguityClassMap();
		NERConfig config = (NERConfig)configuration;
		
//		iterate(configuration.getTSVReader(), inputFiles, nodes -> ac.add(nodes));
		File file = new File("data/prefixData.txt");
		
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(file));
	
			BufferedReader breader = new BufferedReader(reader);
			String line; 
			line = breader.readLine();
			while (line != null){
				String[] lineSplitted = line.split("\t");
				if (lineSplitted.length>2){
					System.out.println("more than one tab!"+line);
				}
				else{
					String[] chunkSplitted = lineSplitted[0].split(" ");
					String[] indexSplitted = lineSplitted[1].split(" ");
					int[] indexs = new int[indexSplitted.length];
					for (int j=0; j<indexSplitted.length; j++)
						indexs[j] = Integer.valueOf(indexSplitted[j]);
					
					String label;
//					for (String word: chunkSplitted)
					for (int k=0; k<chunkSplitted.length;k++){
						String word = chunkSplitted[k];

					
//					String word = lineSplitted[0];
						for (int index: indexs){
							if (chunkSplitted.length == 1)
								label = "U".concat(String.valueOf(index));
							
							else if (k == 0)
								label = "B".concat(String.valueOf(index));
							else if (k == chunkSplitted.length-1)
								label = "L".concat(String.valueOf(index));
							else 
								label = "I".concat(String.valueOf(index));


							
							ac.add(word, label);
						}
					}
				}
				line = breader.readLine();
			}
			breader.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		
		ac.expand(config.getAmbiguityClassThreshold());
		
		BinUtils.LOG.info(String.format("- # of ambiguity classes: %d\n", ac.size()));
		return ac;
	}	
	
	
	
}
