package edu.emory.mathcs.nlp.component.dep;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;
import java.util.Set;
import java.util.HashSet;

import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.util.node.FeatMap;
import edu.emory.mathcs.nlp.component.util.reader.TSVReader;
import edu.emory.mathcs.nlp.component.util.feature.Field;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MyDEPNodeTest
{
	@Test
	public void test() throws Exception
	{
		TSVReader<DEPNode> reader = new TSVReader<>(new DEPIndex(1, 2, 3, 4, 5, 6));
		reader.open(new FileInputStream("src/main/resources/dat/wsj_0001.dep"));
		DEPNode[] nodes = reader.next();
		
		// TODO:
		System.out.println(Joiner.join(nodes, "\n"));
	}
	
	@Test
	public void testDependence()
	{
		DEPNode node1 = new DEPNode(1, "He");
		DEPNode node2 = new DEPNode(2, "bought");
		DEPNode node3 = new DEPNode(3, "a");
		DEPNode node4 = new DEPNode(4, "car");
		DEPNode node5 = new DEPNode(5, "yesterday");
		
		node2.addDependent(node4, "dobj");
		node2.addDependent(node1, "nsubj");
		node4.addDependent(node3, "det");
		node2.addDependent(node5, "tmp");
		
		/*Test the get dep list*/
		List<DEPNode> list = node2.getDependentList();
		assertEquals(node1, list.get(0));
		assertEquals(node4, list.get(1));
		assertEquals(node5, list.get(2));
		
		list = node2.getDependentListByLabel("dobj");
		assertEquals(node4, list.get(0));
		assertEquals(node1,node2.getLeftNearestDependent());
		assertEquals(node4,node2.getRightNearestDependent());
		assertEquals(node5,node2.getRightMostDependent());
	}
	
	@Test
	public void testTree()
	{
		DEPNode node1 = new DEPNode(1, "He");
		DEPNode node2 = new DEPNode(2, "bought");
		DEPNode node3 = new DEPNode(3, "a");
		DEPNode node4 = new DEPNode(4, "car");
		DEPNode node5 = new DEPNode(5, "yesterday");
		
		node2.addDependent(node4, "dobj");
		node2.addDependent(node1, "nsubj");
		node4.addDependent(node3, "det");
		node2.addDependent(node5, "tmp");
		
		assertEquals(node2, node4.getLowestCommonAncestor(node5));
		
		Set<DEPNode> nodeset = new HashSet<>();
		nodeset.add(node2);
		nodeset.add(node4);
		assertEquals(nodeset, node3.getAncestorSet());
		
		/*test categorization*/
		assertEquals(">yesterday>car", node2.getRightSubcategorization(Field.word_form));
        assertEquals("<He", node2.getLeftSubcategorization(Field.word_form));
	}
}

