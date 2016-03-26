/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.blockdetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.audio.AudioManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.geom.Vector3i;
import org.terasology.physics.events.MovedEvent;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem
public class BlockDetectorSystem extends BaseComponentSystem {
    private static Logger logger = LoggerFactory.getLogger(BlockDetectorSystem.class);

    @In
    private WorldProvider worldProvider;

    @In
    private AudioManager audioManager;

    private int detectorRange = 16;

    @ReceiveEvent
    public void getBlocksInRange(MovedEvent event, EntityRef playerEntity) {
        Vector3i blockPosition = new Vector3i(event.getPosition(), RoundingMode.FLOOR);

        Map<BlockUri, Integer> uriCounts = new HashMap<>();

        for (int x = blockPosition.x - detectorRange; x <= blockPosition.x + detectorRange; x++) {
            for (int y = blockPosition.y - detectorRange; y <= blockPosition.y + detectorRange; y++) {
                for (int z = blockPosition.z - detectorRange; z <= blockPosition.z + detectorRange; z++) {
                    Block block = worldProvider.getBlock(new Vector3i(x, y, z));
                    BlockUri uri = block.getURI();
                    uriCounts.put(uri, uriCounts.containsKey(uri) ? uriCounts.get(uri) + 1 : 1);
                }
            }
        }

        Integer blockCount = 0;
        for (BlockUri key : uriCounts.keySet()) {
            blockCount += uriCounts.get(key);
        }

        logger.info("Called BlockDetectorSystem.getBlocksInRange; position=" + event.getPosition());
        logger.info("Total block count: " + blockCount.toString() + " , expected: " + Math.pow(detectorRange * 2 + 1, 3));
        logger.info(uriCounts.toString());
    }
}
