package org.jlab.wedm.persistence.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.business.service.ScreenService;

/**
 *
 * @author ryans
 */
public class FileChangeWatcher implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FileChangeWatcher.class.getName());

    @Override
    public void run() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();

            Path toWatch = Paths.get(EDLParser.EDL_ROOT_DIR);

            toWatch.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            WatchKey key = watcher.take();

            while (key != null) {
                for (WatchEvent event : key.pollEvents()) {
                    LOGGER.log(Level.FINEST, "Received {0} event for file: {1}",
                            new Object[]{event.kind(), event.context()});

                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY || event.kind()
                            == StandardWatchEventKinds.ENTRY_DELETE) {
                        File f = new File((String) event.context());
                        ScreenService.SCREEN_CACHE.remove(f.getCanonicalPath());
                    }
                }

                key.reset();
                key = watcher.take();
            }

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Unable to start file watcher", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "File watcher interrupted", e);
        }
    }

}
