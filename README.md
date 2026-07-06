# SWEN2-Tour-Planner

Tour Planner is a web application for planning and tracking bike, hike, running, and vacation tours, built with a Spring Boot backend and an Angular frontend.
Users can register, create tours with automatically calculated distance and duration (via the OpenRouteService API) and an interactive map (Leaflet), and log completed tours with difficulty, duration, and rating.
Tours and logs are private per user, searchable via full-text search, and can be exported to and re-imported from JSON files.
Authentication is handled via JWT, and all data is persisted in PostgreSQL through Hibernate/JPA with Flyway-managed migrations.