package br.com.guimasnacopa.componentes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.http.ResponseEntity;

public class ObjectsGroup {
		
	
		private List<?> values;

		private Queue<Function<? super Object, ? extends Object>> mappers = new LinkedList<>();
		
		private Map<Integer,Comparator<? super Object>> comparators = new HashMap<>(); // pra ser usado na implementação do order By
	
		private Integer level = 0; //para ser usado na implementação do order by
		
		public ObjectsGroup(List<?> values) {
			this.values = values; 
		}

		public static ObjectsGroup from(List<?> values) {
			ObjectsGroup group = new ObjectsGroup(values);
			return group;
		}
		
		public ObjectsGroup groupBy(Function<? super Object, ? extends Object> mapper) {
			this.level++;
			this.mappers.add(mapper);
			return this;
		}
		
		public ObjectsGroup end() {
			this.level--;
			return this;
		}
		
		public ObjectsGroup orderBy(Comparator<? super Object> comparator) {
			this.comparators.put(level, comparator);
			return this;
		}

		public Map<? extends Object, ?> get() {
			return processesGrouping(this.mappers, this.values);
		}
		
		public List<GroupNode> getNodes() {
			
			System.out.println("Obtendo os nós  Qtd Niveis: " + this.level);
			
			Map<? extends Object, ?> grouping = processesGrouping(this.mappers, this.values);
			List<Object> nodes = convertToNodes(grouping);
			List<GroupNode> result = nodes.stream().map(item->{
				GroupNode node = (GroupNode) item;
				return node;
			}).collect(Collectors.toList());
			Collections.sort(result);
			return result;
			
		}
		

		
		@SuppressWarnings("unchecked")
		private   Map<? extends Object, ?> processesGrouping(Queue <Function<? super Object, ? extends Object>> mappers, List<?> values) {
					
			Map<? extends Object, ?> result = (Map<? extends Object, ?>) values.stream()
					.collect(collectGroups(mappers.iterator()));

			return result;
			
		}
		
		public  List<Object> convertToNodes(Object itens) {
			
			System.out.println("Processando Nivel " + level);
			
			List<Object> nodeList = new ArrayList<>();
			if (itens instanceof HashMap<?, ?>) {
				Map<?, ?> elements = (Map<?, ?>) itens;
				
				
				
					elements.forEach( (key, value) -> {
						GroupNode node = new GroupNode();
						nodeList.add(node);
						node.setNode(key);
						node.setChildren(convertToNodes(value));
					});
					
					this.level--; //indica que um nivel da fila foi consumido
				
				return nodeList;
			} else {
				return (List<Object>) itens; 
			}
			
		}
		
		private static <T,R> Collector<Object, ?, ?> collectGroups(
				Iterator< Function<? super Object, ? extends Object> > mappers ) 
		{
			
				Function<? super Object, ? extends Object> mapper =  mappers.next();
			
				if (mappers.hasNext()) {
					return Collectors.groupingBy(mapper, collectGroups(mappers));
				} 
				
				return Collectors.groupingBy(mapper);

		}
		


	}