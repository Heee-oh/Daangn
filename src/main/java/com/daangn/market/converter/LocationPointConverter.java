package com.daangn.market.converter;

import com.daangn.market.domain.Location;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Converter(autoApply = false)
public class LocationPointConverter implements AttributeConverter<Location, Point> {
    @Override
    public Location convertToEntityAttribute(Point point) {
        if (point == null) return null;
        return new Location(point.getY(), point.getX());
    }

    @Override
    public Point convertToDatabaseColumn(Location location) {
        if (location == null) return null;
        return new GeometryFactory(new PrecisionModel(), 4326)
                .createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));
    }
}
