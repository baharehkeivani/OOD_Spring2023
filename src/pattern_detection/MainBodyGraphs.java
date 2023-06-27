package pattern_detection;

import graph.CustomEdge;
import graph.CustomNode;
import graph.Graph;


public class MainBodyGraphs{
    public MainBodyGraphs() {
        makeMediatorMainBody();
        makeObserverMainBody();
        makeSingletonMainBody();
        makeCompositeMainBody();
        makeDecoratorMainBody();
        makeStateMainBody();
        makeStrategyMainBody();
        makeVisitorMainBody();
        makeClassAdapterMainBody();
        makeObjectAdapterMainBody();
        makeBuilderMainBody();
        makeFactoryMethodMainBody();
        makeAbstractFactoryMainBody();
    }

    public  Graph mediatorGraph = new Graph();
    public  Graph observerGraph = new Graph();
    public  Graph singletonGraph = new Graph();
    public  Graph compositeGraph = new Graph();
    public  Graph decoratorGraph = new Graph();
    public  Graph stateGraph = new Graph();
    public  Graph strategyGraph = new Graph();
    public  Graph visitorGraph = new Graph();
    public  Graph classAdapterGraph = new Graph();
    public  Graph objectAdapterGraph = new Graph();
    public  Graph builderGraph = new Graph();
    public  Graph factoryMethodGraph = new Graph();
    public  Graph abstractFactoryGraph = new Graph();




    private void makeMediatorMainBody(){
        CustomNode m = new CustomNode("Mediator");
        CustomNode c = new CustomNode("Colleague");
        CustomNode cm = new CustomNode("ConcreteMediator");
        CustomNode cc = new CustomNode("ConcreteColleague");

        cm.addEdge(new CustomEdge(cm,m,0,2,3));
        cm.addEdge(new CustomEdge(cm,cc,2,11,13));
        c.addEdge(new CustomEdge(c,m,2,11,13));
        cc.addEdge(new CustomEdge(cc,c,0,2,3));

        mediatorGraph.addNode(m);
        mediatorGraph.addNode(c);
        mediatorGraph.addNode(cm);
        mediatorGraph.addNode(cc);
    }

    private void makeObserverMainBody(){
        CustomNode subject = new CustomNode("Subject");
        CustomNode observer = new CustomNode("Observer");

        subject.addEdge(new CustomEdge(subject,observer,2,11,13));

        observerGraph.addNode(subject);
        observerGraph.addNode(observer);
    }

    private void makeSingletonMainBody(){
        CustomNode singleton = new CustomNode("Singleton");

        singletonGraph.addNode(singleton);
    }

    private void makeCompositeMainBody(){
        CustomNode component = new CustomNode("Component");
        CustomNode composite = new CustomNode("Composite");

        component.addEdge(new CustomEdge(composite,component,0,2,3));
        composite.addEdge(new CustomEdge(composite,component,7,5,7));

        compositeGraph.addNode(component);
        compositeGraph.addNode(composite);
    }

    private void makeDecoratorMainBody(){
        CustomNode component = new CustomNode("Component");
        CustomNode concreteComponent = new CustomNode("ConcreteComponent");
        CustomNode decorator = new CustomNode("Decorator");

        concreteComponent.addEdge(new CustomEdge(concreteComponent,component,0,2,3));
        decorator.addEdge(new CustomEdge(decorator,component,0,2,3));
        decorator.addEdge(new CustomEdge(decorator,component,7,5,7));

        decoratorGraph.addNode(component);
        decoratorGraph.addNode(concreteComponent);
        decoratorGraph.addNode(decorator);
    }

    private void makeStateMainBody(){
        CustomNode context = new CustomNode("Context");
        CustomNode state = new CustomNode("State");
        CustomNode concreteState = new CustomNode("ConcreteState");

        context.addEdge(new CustomEdge(context,state,7,5,7));
        concreteState.addEdge(new CustomEdge(concreteState,state,0,2,3));

        stateGraph.addNode(context);
        stateGraph.addNode(state);
        stateGraph.addNode(concreteState);
    }

    private void makeStrategyMainBody(){
        CustomNode context = new CustomNode("Context");
        CustomNode strategy = new CustomNode("Strategy");
        CustomNode concreteStrategy = new CustomNode("ConcreteStrategy");

        context.addEdge(new CustomEdge(context,strategy,7,5,7));
        concreteStrategy.addEdge(new CustomEdge(concreteStrategy,strategy,0,2,3));

        strategyGraph.addNode(context);
        strategyGraph.addNode(strategy);
        strategyGraph.addNode(concreteStrategy);
    }

    private void makeVisitorMainBody(){
        CustomNode client = new CustomNode("Client");
        CustomNode visitor = new CustomNode("Visitor");
        CustomNode concreteVisitor = new CustomNode("ConcreteVisitor");
        CustomNode objectStructure = new CustomNode("ObjectStructure");
        CustomNode element = new CustomNode("Element");
        CustomNode concreteElement = new CustomNode("ConcreteElement");

        client.addEdge(new CustomEdge(client,visitor,2,11,13));
        client.addEdge(new CustomEdge(client,objectStructure,2,11,13));
        objectStructure.addEdge(new CustomEdge(objectStructure,element,2,11,13));
        concreteVisitor.addEdge(new CustomEdge(concreteVisitor,visitor,0,2,3));
        concreteElement.addEdge(new CustomEdge(concreteElement,element,0,2,3));

        visitorGraph.addNode(client);
        visitorGraph.addNode(visitor);
        visitorGraph.addNode(concreteVisitor);
        visitorGraph.addNode(objectStructure);
        visitorGraph.addNode(element);
        visitorGraph.addNode(concreteElement);

    }

    private void makeClassAdapterMainBody(){
        CustomNode target = new CustomNode("Target");
        CustomNode adapter = new CustomNode("Adapter");
        CustomNode adaptee = new CustomNode("Adaptee");

        adapter.addEdge(new CustomEdge(adapter,target,0,2,3));
        adapter.addEdge(new CustomEdge(adapter,adaptee,0,2,3));

        classAdapterGraph.addNode(target);
        classAdapterGraph.addNode(adapter);
        classAdapterGraph.addNode(adaptee);
    }

    private void makeObjectAdapterMainBody(){
        CustomNode target = new CustomNode("Target");
        CustomNode adapter = new CustomNode("Adapter");
        CustomNode adaptee = new CustomNode("Adaptee");

        adapter.addEdge(new CustomEdge(adapter,target,0,2,3));
        adapter.addEdge(new CustomEdge(adapter,adaptee,2,11,13));

        objectAdapterGraph.addNode(target);
        objectAdapterGraph.addNode(adapter);
        objectAdapterGraph.addNode(adaptee);
    }

    private void makeBuilderMainBody(){
        CustomNode director = new CustomNode("Director");
        CustomNode builder = new CustomNode("Builder");
        CustomNode concreteBuilder = new CustomNode("ConcreteBuilder");
        CustomNode product = new CustomNode("Product");

        director.addEdge(new CustomEdge(director,builder,7,5,7));
        concreteBuilder.addEdge(new CustomEdge(concreteBuilder,builder,0,2,3));
        concreteBuilder.addEdge(new CustomEdge(concreteBuilder,product,2,11,13));

        builderGraph.addNode(director);
        builderGraph.addNode(builder);
        builderGraph.addNode(concreteBuilder);
        builderGraph.addNode(product);
    }

    private void makeFactoryMethodMainBody(){
        CustomNode creator = new CustomNode("Creator");
        CustomNode concreteProduct = new CustomNode("ConcreteProduct");
        CustomNode concreteCreator = new CustomNode("concreteCreator");

        concreteCreator.addEdge(new CustomEdge(concreteCreator,creator,0,2,3));
        concreteCreator.addEdge(new CustomEdge(concreteCreator,concreteProduct,2,11,13));

        factoryMethodGraph.addNode(creator);
        factoryMethodGraph.addNode(concreteProduct);
        factoryMethodGraph.addNode(concreteCreator);
    }

    private void makeAbstractFactoryMainBody(){
        CustomNode abstractFactory = new CustomNode("AbstractFactory");
        CustomNode client = new CustomNode("Client");
        CustomNode concreteFactory = new CustomNode("ConcreteFactory");
        CustomNode product = new CustomNode("Product");

        client.addEdge(new CustomEdge(client,abstractFactory,2,11,13));
        client.addEdge(new CustomEdge(client,product,2,11,13));
        concreteFactory.addEdge(new CustomEdge(concreteFactory,product,2,11,13));
        concreteFactory.addEdge(new CustomEdge(concreteFactory,abstractFactory,0,2,3));

        abstractFactoryGraph.addNode(abstractFactory);
        abstractFactoryGraph.addNode(client);
        abstractFactoryGraph.addNode(concreteFactory);
        abstractFactoryGraph.addNode(product);
    }

}