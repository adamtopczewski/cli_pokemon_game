package com.example;
import com.example.models.GameState;
import com.example.models.Player;
import com.example.state.IntroState;
import com.example.state.SceneManager;
import com.example.ui.GameTerminal;

public class App 
{
    public static void main( String[] args ) {
        // https://stackoverflow.com/a/66081835 - FPS implementation (w/o update branch);
        final int MAX_FPS = 30;

        final long drawThreshold = 1000000000 / MAX_FPS;

        long lastFPS = 0, lastFPOutput = 0;

        int fps =0;

        try {
            SceneManager sceneManager = new SceneManager(new GameTerminal().getTerminal(), new Player(0,0));
            sceneManager.changeState(new IntroState(sceneManager));
            // todo refactor? (abstrakcja kontroli fps od obsługi stanu)
            while (sceneManager.isRunning()) {

                if ((System.nanoTime() - lastFPOutput) > 1000000000) {
                    System.out.println("FPS: " + (double)fps);

                    fps = 0;

                    lastFPOutput = System.nanoTime();
                }

                if( (System.nanoTime() - lastFPS) > drawThreshold)
                {
                    lastFPS = System.nanoTime();
                    GameState currentState = sceneManager.getCurrentState();
                    currentState.render();
                    currentState.handleInput();
                    fps++;
                }

                // Calculate next frame, or skip if we are running behind
                if(!((System.nanoTime() - lastFPS) > drawThreshold))
                {
                    long nextScheduledDraw = lastFPS + drawThreshold;

                    long minScheduled = nextScheduledDraw;

                    long nanosToWait = minScheduled - System.nanoTime();

                    // Just in cases
                    if(nanosToWait <= 0)
                        continue;

                    try
                    {
                        Thread.sleep(nanosToWait / 1000000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }


            }
        } catch (Exception e) {
            // TODO: handle exceptions
            System.out.println("Exception " + e.getClass() + ": " + e.getMessage());
        }

    }
}


