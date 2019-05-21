package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import com.google.common.eventbus.Subscribe;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.misc.AnimationSpeed;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * The widget, which handles the state and transition settings of the automaton.
 * @author József Pollák
 */
public class StateEditorPane extends AnchorPane {

    Timeline lastAnim = null;

    private final List<CANode> nodes;
    private final List<CANode> removeNodes;
    private final List<CAEdge> edges;
    private final IntegerProperty nodeCount;
    private Circle startCircle;
    private Circle endCircle;
    private Line drawLine;
    private final ObjectProperty<CAEdge> selectedEdgeProperty;

    public StateEditorPane(EditPane parent, double width, double height) {
        setPrefSize(width, height);
        nodes = new ArrayList<>(10);
        removeNodes = new ArrayList<>();
        edges = new ArrayList<>(10);

        nodeCount = new SimpleIntegerProperty();
        nodeCount.addListener(this::nodesChanged);

        selectedEdgeProperty = new SimpleObjectProperty<>();
        initListeners();
    }

    private void initListeners() {
        setOnMousePressed(this::mousePress);
        setOnMouseReleased(this::mouseRelease);
        setOnMouseDragged(this::mouseDrag);
        setOnMouseClicked(this::mouseClick);
    }

    private void nodesChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (lastAnim != null) {
            lastAnim.stop();
        }
        double midX = getPrefWidth() / 2;
        double midY = getPrefHeight() / 2;
        final int newCount = nodeCount.get();
        lastAnim = new Timeline();
        final int countDifference = newCount - nodes.size();

        if (isNodeAdded(newCount)) {
            handleNodesAdded(countDifference, midX, midY);
        } else if (isNodeRemoved(newCount)) {
            handleNodesRemoved();
        }
        KeyFrame kf;
        final double step = Math.PI * 2 / newCount;
        double angle = 0;
        for (int i = 0; i < nodes.size(); ++i) {
            CANode c = nodes.get(i);
            double distanceFromCenter = midX * .9;
            double newX = distanceFromCenter * Math.cos(angle) + midX;
            double newY = distanceFromCenter * Math.sin(angle) + midY;
            KeyValue kv1 = new KeyValue(c.getCenterXProperty(), newX);
            KeyValue kv2 = new KeyValue(c.getCenterYProperty(), newY);
            angle += step;
            kf = new KeyFrame(Duration.millis(AnimationSpeed.ANIMATION_MEDIUM.speed()), kv1, kv2);
            lastAnim.getKeyFrames().add(kf);
        }
        for (CANode node : removeNodes) {
            double distanceFromCenter = midX * .9;
            double newX = distanceFromCenter * Math.cos(0) + midX;
            double newY = distanceFromCenter * Math.sin(0) + midY;
            KeyValue kv1 = new KeyValue(node.getCenterXProperty(), newX);
            KeyValue kv2 = new KeyValue(node.getCenterYProperty(), newY);

            kf = new KeyFrame(Duration.millis(AnimationSpeed.ANIMATION_MEDIUM.speed()), kv1, kv2);
            lastAnim.getKeyFrames().add(kf);
        }
        lastAnim.play();
    }

    private void handleNodesRemoved() {
        int diff = nodes.size() - nodeCount.get();
        int oldSize = nodes.size();
        for (int i = nodes.size() - 1; i >= oldSize - diff; --i) {
            CANode node = nodes.get(i);
            nodes.remove(node);
            removeNodes.add(node);
        }
        lastAnim.setOnFinished(this::nodesRemovedCallback);
    }

    private void handleNodesAdded(final int countDifference, double midX, double midY) {
        CANode last = null;
        if (!nodes.isEmpty()) {
            last = nodes.get(nodes.size() - 1);
        }
        
        for (int i = 0; i < countDifference; ++i) {
            int shift = countDifference - i;
            CANode c;
            if (last != null) {
                c = new CANode(nodeCount.get() - shift, last.getCenterX(), last.getCenterY(), last.getRadius());
            } else {
                c = new CANode(nodeCount.get() - shift, midX, midY, 13);
            }
            nodes.add(c);
            getChildren().add(c);
            c.toFront();
        }
        //make sure the first node is always on top
        nodes.get(0).toFront();
    }

    private void nodesRemovedCallback(ActionEvent event) {
        for (Iterator<CANode> i = removeNodes.iterator(); i.hasNext();) {
            CANode node = i.next();
            for (int j = edges.size() - 1; j >= 0; --j) {
                CAEdge edge = edges.get(j);
                if (edge.contains(node)) {
                    removeEdge(edge);
                }
            }
            getChildren().remove(node.getOutline());
            getChildren().remove(node);
            i.remove();
        }
    }

    private boolean isNodeRemoved(final int n) {
        return nodes.size() > n;
    }

    private boolean isNodeAdded(final int n) {
        return nodes.size() < n;
    }

    public int getNodeCount() {
        return nodeCount.intValue();
    }

    public void setNodeCount(int i) {
        nodeCount.set(i);
    }

    public void incNodeCount() {
        nodeCount.set(nodeCount.get() + 1);
    }

    public void decNodeCount() {
        if (nodeCount.get() > 0) {
            nodeCount.set(nodeCount.get() - 1);
        }
    }

    public IntegerProperty nodeCountProperty() {
        return nodeCount;
    }

    public ObjectProperty<CAEdge> selectedEdgeProperty() {
        return selectedEdgeProperty;
    }

    private void mousePress(MouseEvent e) {
        if (e.getTarget().getClass() == Circle.class) {
            boolean isValidNode = getOptionalCANodeFromList((Circle) e.getTarget(), nodes).isPresent();

            if (isValidNode) {
                startCircle = (Circle) e.getTarget();
                drawLine = new Line(e.getX(), e.getY(), e.getX(), e.getY());
                drawLine.startXProperty().set(startCircle.centerXProperty().doubleValue());
                drawLine.startYProperty().set(startCircle.centerYProperty().doubleValue());
                getChildren().add(drawLine);
                drawLine.toBack();
            }
        }
    }

    private void mouseRelease(MouseEvent e) {
        if (startCircle != null && endCircle != null && startCircle != endCircle) {
            CANode start = getOptionalCANodeFromList(startCircle, nodes).get();
            CANode end = getOptionalCANodeFromList(endCircle, nodes).get();
            addEdge(new CAEdge(drawLine, start, end, nodeCount));
        } else {
            getChildren().remove(drawLine);
        }
        drawLine = null;
        startCircle = endCircle = null;
    }

    private void addEdge(CAEdge newEdge) {
        if (!edges.contains(newEdge)) {
            edges.add(newEdge);
            getChildren().add(newEdge);
            newEdge.toBack();
        }
    }

    private void mouseClick(MouseEvent event) {
        if (event.getTarget().getClass() == Circle.class) {
            Optional<CAEdge> edge = getOptionalCAEdgeFromList((Circle) event.getTarget(), edges);
            if (edge.isPresent()) {
                if (!edge.get().equals(selectedEdgeProperty.get())) {
                    handleShrink();
                }
                selectedEdgeProperty.set(edge.get());
                selectedEdgeProperty.get().grow();
            } else {
                handleShrink();
            }
        } else {
            handleShrink();
        }
    }

    private void handleShrink() {
        if (selectedEdgeProperty.get() != null) {
            selectedEdgeProperty.get().shrink();
        }
        selectedEdgeProperty.set(null);
    }

    private void mouseDrag(MouseEvent e) {
        if (e.getX() < 0 || getPrefWidth() < e.getX() || e.getY() < 0 || getPrefHeight() < e.getY()) {
            getChildren().remove(drawLine);
            drawLine = null;
        }
        if (drawLine != null) {
            drawLine.setEndX(e.getX());
            drawLine.setEndY(e.getY());
            Optional<Circle> optional = overNode(e.getX(), e.getY(), 20);
            if (optional.isPresent() && optional.get() != startCircle) {
                Circle c = optional.get();
                drawLine.setEndX(c.getCenterX());
                drawLine.setEndY(c.getCenterY());
                endCircle = c;
            } else {
                endCircle = null;
            }
        }
    }

    private Optional<CANode> getOptionalCANodeFromList(Circle node, List<CANode> list) {
        return list.stream().filter(c -> c.hasCircle(node)).findFirst();
    }

    private Optional<CAEdge> getOptionalCAEdgeFromList(Circle node, List<CAEdge> list) {
        return list.stream().filter(c -> c.hasDirectionIndicator(node)).findFirst();
    }

    public Optional<Circle> overNode(double clickX, double clickY, double circleDiameter) {
        for (CANode c : nodes) {
            if (c.contains(clickX, clickY)) {
                return Optional.of(c.getInnerCircle());
            }
        }
        return Optional.empty();
    }

    /**
     * Subscription method for the EventBus that listens for edge removal events.
     * @param edge the edge to remove
     */
    @Subscribe
    public void removeEdge(CAEdge edge) {
        edge.unbind();
        edges.remove(edge);
        Timeline lineRemoval = edge.getRemovalAnimation();
        lineRemoval.setOnFinished((ActionEvent event1) -> {
            getChildren().remove(edge);
        });
        lineRemoval.play();
    }

    /**
     * Removes all transitions.
     */
    public void removeEdges() {
        for (int i = edges.size() - 1; i >= 0; --i) {
            removeEdge(edges.get(i));
        }
    }

    /**
     * Loads the settings into the widget.
     * @param transitions the settings
     */
    public void load(Map<Integer, List<Rule>> transitions) {
        removeEdges();
        setNodeCount(1);
        Runnable loadProcess = () -> {
            transitions.keySet().stream().forEach((key) -> {
                int from = key;
                List<Rule> rules = transitions.get(key);
                rules.stream().forEach((rule) -> {
                    int to = rule.getNextState();
                    if (from >= 0 && to < nodes.size()) {
                        String content = rule.getExpressions().stream().collect(Collectors.joining("\n"));
                        CAEdge edge = new CAEdge(
                                new Line(),
                                nodes.get(from),
                                nodes.get(to),
                                nodeCount,
                                content);
                        addEdge(edge);
                        edge.toBack();
                    }
                });
            });
        };
        if (nodeCount.get() == transitions.size()) {
            loadProcess.run();
        } else {
            setNodeCount(transitions.size());
            lastAnim.setOnFinished(event -> {
                loadProcess.run();
            });
        }
    }

    /**
     * Returns the transition rules.
     * @return the transition rules, in that format, which the CellularAutomaton2D object uses.
     */
    public Map<Integer, List<Rule>> getTransitionRules() {
        Map<Integer, List<Rule>> transitionRules = new HashMap<>();

        nodes.forEach(stateNode -> {
            List<Rule> rules = new ArrayList<>();

            edges
                    .stream()
                    .filter(edge -> edge.getStart().equals(stateNode))
                    .forEach(outEdge -> {

                        List<String> expressions = Arrays.stream(
                                outEdge
                                .getRulesProperty()
                                .get()
                                .split("\n"))
                        .map(rule -> rule.replaceAll(" ", ""))
                        .filter(rule -> !"".equals(rule))
                        .collect(Collectors.toList());

                        rules.add(new Rule(expressions, outEdge.getEnd().getStateNumber()));

                    });

            transitionRules.put(stateNode.getStateNumber(), rules);
        });

        return transitionRules;
    }

    private String listElements(List<String> s) {
        return s.stream().collect(Collectors.joining(", "));
    }
    
    /**
     * Returns the list of colors currently used for the states.
     * @return the list of colors currently used for the states
     */
    public List<Color> getStateColors() {
        return nodes.stream().map(e -> (Color) e.getInnerCircle().getFill()).collect(Collectors.toList());
    }

}
