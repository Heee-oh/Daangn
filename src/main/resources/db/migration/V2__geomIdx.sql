CREATE INDEX idx_region_geom_gist ON region USING GIST (geom);
ANALYZE region; --통계 재계산--

