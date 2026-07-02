package br.com.guimasnacopa.componentes;

import java.util.List;
import java.util.stream.Collectors;

public class GroupNode implements Comparable<GroupNode> {
	
	private Object node;
	
	private List<Object> children;

	public Object getNode() {
		return node;
	}

	public void setNode(Object node) {
		this.node = node;
	}

	public List<Object> getChildren() {
		if (children == null || children.isEmpty()) {
			return children;
		}

		Object primeiroFilho = children.get(0);
		if (primeiroFilho instanceof GroupNode) {
			return children.stream().sorted().collect(Collectors.toList());
		}

		// Folhas (ex.: lista de Palpite) devem manter a ordem já calculada no controller.
		return children;
	}
	
	public List<Object> getChildrenAsLastLevel() {
		return getChildren()
				.stream()
				.map(gn -> ((GroupNode) gn).getNode())
				.sorted
				().collect(Collectors.toList());
	}

	public void setChildren(List<Object> children) {
		this.children = children;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(GroupNode other) {
		if (this.getNode() instanceof Comparable<?> && other.getNode() instanceof Comparable<?>) {
			Comparable<Object> thisComparable = (Comparable<Object>) this.getNode();
			Comparable<Object> otherComparable = (Comparable<Object>) other.getNode();
			return thisComparable.compareTo(otherComparable);
		}
		return 1;
	}

	

}
