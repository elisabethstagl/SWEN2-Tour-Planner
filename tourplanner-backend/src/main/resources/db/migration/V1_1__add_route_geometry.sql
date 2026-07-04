ALTER TABLE tours
    ADD COLUMN route_geometry TEXT;

ALTER TABLE tours
DROP COLUMN IF EXISTS map_image_path;