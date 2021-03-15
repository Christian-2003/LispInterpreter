package parser;



/**
 * Modellierung eines Binaerbaums.
 * 
 * @version	07.01.2021
 * 
 * @author	Christian S
 */
public class BinaryTree<ContentType> {
	/**
	 * Modelliert einen Knoten in einem Binaerbaum.
	 */
	private class Node {
		/**
		 * Speichert den Inhalt des Knoten.
		 */
		private ContentType cContent;
		
		/**
		 * Speichert den linken Teilbaum.
		 */
		private BinaryTree<ContentType> tcLeftSubTree;
		
		/**
		 * Speichert den rechten Teilbaum.
		 */
		private BinaryTree<ContentType> tcRightSubTree;
		
		
		
		/**
		 * Konstruktor der Klasse "CNode".
		 * 
		 * @param pcContent	Inhalt des Knotens.
		 */
		private Node(ContentType pcContent) {
			this.cContent = pcContent; //Inhalt des Knoten hinzufuegen.
			this.tcLeftSubTree = new BinaryTree<ContentType>(); //Linken Teilbaum ibstanziieren.
			this.tcRightSubTree = new BinaryTree<ContentType>(); //Rechten Teilbaum instanziieren.
		}
	}
	
	
	
	/**
	 * Knoten des Binaerbaums.
	 */
	private Node nodeObj;
	
	
	
	/**
	 * Konstruktor der Klasse "CBinaryTree".
	 * Erzeugt einen leeren Binaerbaum.
	 */
	public BinaryTree() {
		this.nodeObj = null;
	}
	
	/**
	 * Erzeugt einen Binaerbaum mit dem Parameter pcContent als Inhalt und zwei leeren
	 * Teilbaeumen. Wenn der Parameter pcContent null ist, dann wird ein leerer Binaerbaum
	 * erzeugt.
	 * 
	 * @param pcContent	Inahlt des Knotens.
	 */
	public BinaryTree(ContentType pcContent) {
		if (pcContent == null) {
			//Inhalt ist null, also sollen keine Teilbaeume erzeugt werden:
			this.nodeObj = null;
		}
		
		else {
			//Inhalt ist nicht null, also wird neuer Binaerbaum erzeugt:
			this.nodeObj = new Node(pcContent);
		}
	}
	
	/**
	 * Erzeugt einen Binaerbaum, mit pcContent als Inhalt und den Baeumen ptcLeftSubTree und
	 * ptcRightSubTree als Teilbaeumen. Wenn der Parameter pcContent null ist, wird ein leerer
	 * Binaerbaum erzeugt. Wenn einer der Teilbaeume null ist, dann wird entsprechend ein leerer
	 * Binaerbaum an dessen Stelle eingefuegt.
	 * 
	 * @param pcContent			Inhalt des Knotens.
	 * @param ptcLeftSubTree	Linker Teilbaum.
	 * @param ptcRightSubTree	Rechter Teilbaum.
	 */
	public BinaryTree(ContentType pcContent, BinaryTree<ContentType> ptcLeftSubTree, BinaryTree<ContentType> ptcRightSubTree) {
		if (pcContent == null) {
			//Inhalt ist null, also sollen keine Teilbaeume erzeugt werden:
			this.nodeObj = null;
		}
		
		else {
			//Inhalt ist nicht null, also wird neuer Binaerbaum erzeugt:
			this.nodeObj = new Node(pcContent);
			
			if (ptcLeftSubTree == null) {
				//Linker Teilbum ist null -> Leeren Binaerbaum einfuegen:
				this.nodeObj.tcLeftSubTree = new BinaryTree<ContentType>();
			}
			else {
				//Linker Teilbum ist nicht null:
				this.nodeObj.tcLeftSubTree = ptcLeftSubTree;
			}
			
			if (ptcRightSubTree == null) {
				//Rechter Teilbaum ist null -> Leeren Binaerbaum einfuegen:
				this.nodeObj.tcRightSubTree = new BinaryTree<ContentType>();
			}
			else {
				//Rechter Teilbum ist nicht null:
				this.nodeObj.tcRightSubTree = ptcRightSubTree;
			}
		}
	}
	
	
	
	/**
	 * Ersetzt den Inhalt des Knotens mit dem Wert des Parameters pcNewContent. Wenn der
	 * Binaerbaum leer ist, werden zwei neue Teilbaeume erzeugt.
	 * 
	 * @param pcNewContent	Neuer Inhalt fuer den Knoten.
	 */
	public void setContent(ContentType pcNewContent) {
		if (pcNewContent != null) {
			//Neuer Inhalt ist nicht null -> Kann hinzugefuegt werden:
			if (this.isEmpty()) {
				//Akteuller Binaerbaum ist leer -> neuen erzeugen:
				this.nodeObj = new Node(pcNewContent);
				this.nodeObj.tcLeftSubTree = new BinaryTree<ContentType>();
				this.nodeObj.tcRightSubTree = new BinaryTree<ContentType>();
			}
			else {
				//Inhalt muss nur ersetzt werden:
				this.nodeObj.cContent = pcNewContent;
			}
		}
	}
	
	/**
	 * Ersetzt den linken Teilbaum mit dem Parameter ptcNewLeftSubTree wenn dieser Parameter
	 * nicht null ist oder der Binaerbaum nicht leer ist.
	 * 
	 * @param ptcNewLeftSubTree	Neuer linker Teilbaum.
	 */
	public void setLeftSubTree(BinaryTree<ContentType> ptcNewLeftSubTree) {
		if (!this.isEmpty() && ptcNewLeftSubTree != null) {
			//Neuer Teilbaum ist nicht null und Binaerbaum ist nicht leer:
			this.nodeObj.tcLeftSubTree = ptcNewLeftSubTree;
		}
	}
	
	/**
	 * Ersetzt den rechten Teilbaum mit dem Parameter ptcNewRightSubTree wenn dieser Parameter
	 * nicht null ist oder der Binaerbaum nicht leer ist.
	 * 
	 * @param ptcNewRightSubTree	Neuer rechter Teilbaum.
	 */
	public void setRightSubTree(BinaryTree<ContentType> ptcNewRightSubTree) {
		if (!this.isEmpty() && ptcNewRightSubTree != null) {
			//Neuer Teilbaum ist nicht null und Binaerbaum ist nicht leer:
			this.nodeObj.tcRightSubTree = ptcNewRightSubTree;
		}
	}
	
	
	
	/**
	 * Gibt den Inhalt des aktuellen Knoten zuerueck. Wenn der Binaerbaum leer ist, wird
	 * null zurueckgegeben.
	 * 
	 * @return	Inhalt des aktuellen Knotens.
	 */
	public ContentType getContent() {
		if (this.isEmpty()) {
			//Binaerbaum ist leer:
			return null;
		}
		else {
			//Binaerbaum ist nicht leer:
			return this.nodeObj.cContent;
		}
	}
	
	/**
	 * Gibt den linken Teilbaum zurueck, wenn dieser vorhanden ist.
	 * 
	 * @return	Linker Teilbaum.
	 */
	public BinaryTree<ContentType> getLeftSubTree() {
		if (this.isEmpty()) {
			//Binaerbaum ist leer:
			return null;
		}
		//Binaerbaum ist nicht leer:
		return this.nodeObj.tcLeftSubTree;
	}
	
	/**
	 * Gibt den rechten Teilbaum zurueck, wenn dieser vorhanden ist.
	 * 
	 * @return	Rechter Teilbaum.
	 */
	public BinaryTree<ContentType> getRightSubTree() {
		if (this.isEmpty()) {
			//Binaerbaum ist leer:
			return null;
		}
		//Binaerbaum ist nicht leer:
		return this.nodeObj.tcRightSubTree;
	}
	
	/**
	 * Gibt den Inhalt des linken Teilbaums zurueck. Wenn kein linker Teilbaum vorhanden ist,
	 * wird null zureuckgegeben.
	 * 
	 * @return	Inhalt des linken Teilbaums.
	 */
	public ContentType getLeftChild() {
		if (this.getLeftSubTree() == null) {
			//Es gibt keinen linken Teilbaum:
			return null;
		}
		else {
			//Linker Teilbaum vorhanden:
			return this.getLeftSubTree().getContent();
		}
	}
	
	/**
	 * Gibt den Inhalt des rechten Teilbaums zurueck. Wenn kein rechter Teilbaum vorhanden ist,
	 * wird null zureuckgegeben.
	 * 
	 * @return	Inhalt des rechten Teilbaums.
	 */
	public ContentType getRightChild() {
		if (this.getRightSubTree() == null) {
			//Es gibt keinen rechten Teilbaum:
			return null;
		}
		else {
			//Rechter Teilbaum vorhanden:
			return this.getRightSubTree().getContent();
		}
	}
	
	
	
	/**
	 * Gibt an, ob der Binaerbaum leer ist.
	 * 
	 * @return	true, wenn der Binaerbaum leer ist;
	 * 			false, wenn der Binaerbaum nicht leer ist.
	 */
	public boolean isEmpty() {
		if (this.nodeObj == null) {
			//Knoten des Binaerbaums ist null -> also ist der Binaerbaum leer:
			return true;
		}
		//Knoten des Binaerbaums ist nicht null -> also ist der Binaerbaum nicht leer:
		return false;
	}
	
	
	
	/**
	public BinaryTree<ContentType> copyTree() {
		BinaryTree<ContentType> tcCopiedTree = new BinaryTree<ContentType>(this.nodeObj.cContent); //Erstellt einen neuen Teilbaum, mit dem aktuellen Knoten als Inhalt.
		if (getLeftChild() != null) {
			//Knoten des linken Teilbaums ist nicht null -> Teilbaum kann angehaengt werden:
			tcCopiedTree.nodeObj.tcLeftSubTree = this.getLeftSubTree().copyTree(); //Kopiert den linken Teilbaum.
		}
		if (getRightChild() != null) {
			//Knoten des rechten Teilbaums ist nicht null -> Teilbaum kann angehaengt werden:
			tcCopiedTree.nodeObj.tcRightSubTree = this.getRightSubTree().copyTree(); //Kopiert den rechten Teilbaum.
		}
		return tcCopiedTree;
	}
	*/
}
