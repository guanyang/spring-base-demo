package org.gy.demo.redisdemo;

import java.util.*;

public class EventTest {

    interface Event<T> {
        T getData();
    }

    interface EventListener<E extends Event<?>> {

        Class<E> getEventType();

        void onEvent(E event);
    }

    public static class EventOneHandler implements EventListener<EventOne> {
        @Override
        public Class<EventOne> getEventType() {
            return EventOne.class;
        }

        @Override
        public void onEvent(EventOne event) {
            System.out.println("EventOneHandler: " + event.getData());
        }
    }

    public static class EventHandler implements EventListener<EventOne> {
        @Override
        public Class<EventOne> getEventType() {
            return EventOne.class;
        }

        @Override
        public void onEvent(EventOne event) {
            System.out.println("EventHandler: " + event.getData() + "\t" + System.currentTimeMillis());
        }
    }

    public static class EventTwoHandler implements EventListener<EventTwo> {
        @Override
        public Class<EventTwo> getEventType() {
            return EventTwo.class;
        }

        @Override
        public void onEvent(EventTwo event) {
            System.out.println("EventTwoHandler: " + event.getData());
        }
    }

    interface EventBus {
        <E extends Event<?>> void register(EventListener<E> listener);

        <E extends Event<?>> void unregister(EventListener<E> listener);

        <T> void post(Event<T> event);
    }

    public static class SimpleEventBus implements EventBus {

        private final Map<Class<? extends Event<?>>, List<EventListener<? extends Event<?>>>> listenerMap = new HashMap<>();

        @Override
        public <E extends Event<?>> void register(EventListener<E> listener) {
            Optional.ofNullable(listener).ifPresent(l -> {
                List<EventListener<? extends Event<?>>> listeners = listenerMap.computeIfAbsent(l.getEventType(), k -> new ArrayList<>());
                listeners.add(l);
            });
        }

        @Override
        public <E extends Event<?>> void unregister(EventListener<E> listener) {
            Optional.ofNullable(listener).ifPresent(l -> {
                List<EventListener<? extends Event<?>>> listeners = listenerMap.computeIfAbsent(l.getEventType(), k -> new ArrayList<>());
                listeners.remove(l);
            });
        }

        @Override
        public <T> void post(Event<T> event) {
            Optional.ofNullable(event).ifPresent(e -> {
                List<EventListener<? extends Event<?>>> listeners = listenerMap.get(e.getClass());
                if (listeners != null) {
                    listeners.forEach(l -> ((EventListener<Event<?>>) l).onEvent(e));
                }
            });
        }
    }

    public static void main(String[] args) {
        SimpleEventBus eventBus = new SimpleEventBus();
        eventBus.register(new EventOneHandler());
        eventBus.register(new EventHandler());
        eventBus.register(new EventTwoHandler());
        eventBus.post(new EventOne("Hello"));
        eventBus.post(new EventTwo(1));
    }


    public static class EventOne implements Event<String> {
        private final String data;

        public EventOne(String data) {
            this.data = data;
        }

        @Override
        public String getData() {
            return data;
        }
    }

    public static class EventTwo implements Event<Integer> {
        private final Integer data;

        public EventTwo(Integer data) {
            this.data = data;
        }

        @Override
        public Integer getData() {
            return data;
        }
    }
}
