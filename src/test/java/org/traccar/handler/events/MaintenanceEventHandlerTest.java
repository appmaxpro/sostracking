package org.traccar.handler.events;

import org.junit.jupiter.api.Test;
import org.traccar.BaseTest;
import org.traccar.model.Maintenance;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;

public class MaintenanceEventHandlerTest extends BaseTest {
    
    @Test
    public void testMaintenanceEventHandler() {
        Position lastPosition = new Position();
        lastPosition.setDeviceId(1);
        lastPosition.setFixTime(new Date(0));

        Position position = new Position();
        position.setDeviceId(1);
        position.setFixTime(new Date(0));

        var maintenance = mock(Maintenance.class);
        when(maintenance.getType()).thenReturn(Position.KEY_TOTAL_DISTANCE);
        var maintenances = Arrays.asList(maintenance);

        var cacheManager = mock(CacheManager.class);
        when(cacheManager.getDeviceObjects(anyLong(), eq(Maintenance.class))).thenReturn(maintenances);
        when(cacheManager.getPosition(anyLong())).thenReturn(lastPosition);
        MaintenanceEventHandler eventHandler = new MaintenanceEventHandler(cacheManager);        

        when(maintenance.getStart()).thenReturn(10000.0);
        when(maintenance.getPeriod()).thenReturn(2000.0);                
 
        lastPosition.set(Position.KEY_TOTAL_DISTANCE, 1999);
        position.set(Position.KEY_TOTAL_DISTANCE, 2001);        
        assertTrue(eventHandler.analyzePosition(position).isEmpty());

        lastPosition.set(Position.KEY_TOTAL_DISTANCE, 3999);
        position.set(Position.KEY_TOTAL_DISTANCE, 4001);        
        assertTrue(eventHandler.analyzePosition(position).isEmpty());

        lastPosition.set(Position.KEY_TOTAL_DISTANCE, 9999);
        position.set(Position.KEY_TOTAL_DISTANCE, 10001);        
        assertTrue(eventHandler.analyzePosition(position).size() == 1);

        lastPosition.set(Position.KEY_TOTAL_DISTANCE, 11999);
        position.set(Position.KEY_TOTAL_DISTANCE, 12001);        
        assertTrue(eventHandler.analyzePosition(position).size() == 1);
        
    }

}
