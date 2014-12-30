/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package chemitaxis;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;

/**
 * Created by cbadenes on 05/12/14.
 */
public class ChemitaxisSim extends SimState {

    public double width             = 7.0;      // 7.0
    public double height            = 7.0;      // 7.0
    public double particleWidth     = 0.06;     // 0.06

    public Continuous2D space;
    public Continuous2D area;

    private RadiationParticle[] radiationParticles;
    private InsulationParticle[] insulationParticles;

    private int numRadioactiveParticles = 100;  // 100
    private int numInsulationParticles  = 700;  // 700
    private int radiationIntensity      = 4;    // 4

    private double radiationRadius  = 0.15;     // 0.15
    private double joiningRadius    = 3.5;      // 3.5
    private double maxVelocity      = 0.06;     // 0.06

    private int movementHistory     = 3;
    // Properties

    public double getJoiningRadius() {
        return joiningRadius;
    }
    public void setJoiningRadius(double joiningRadius) {
        this.joiningRadius = joiningRadius;
    }
    public double getRadiationRadius() {
        return radiationRadius;
    }
    public void setRadiationRadius(double radiationRadius) {
        this.radiationRadius = radiationRadius;
    }
    public double getMaxVelocity() {
        return maxVelocity;
    }
    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }
    public int getNumRadioactiveParticles() {
        return numRadioactiveParticles;
    }
    public void setNumRadioactiveParticles(int numRadioactiveParticles) {
        this.numRadioactiveParticles = numRadioactiveParticles;
    }
    public int getNumInsulationParticles() {
        return numInsulationParticles;
    }
    public void setNumInsulationParticles(int numInsulationParticles) {
        this.numInsulationParticles = numInsulationParticles;
    }
    public int getRadiationIntensity() {
        return radiationIntensity;
    }
    public void setRadiationIntensity(int radiationIntensity) {
        this.radiationIntensity = radiationIntensity;
    }
    public int getMovementHistory() {
        return movementHistory;
    }
    public void setMovementHistory(int movementHistory) {
        this.movementHistory = movementHistory;
    }

    public ChemitaxisSim(long seed) {
        super(seed);
    }

    private Particle initializeParticle(Particle particle){
        schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdateRadiation();
            }
        });

        schedule.scheduleRepeating(Schedule.EPOCH, 2, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdateVelocity();
            }
        });

        schedule.scheduleRepeating(Schedule.EPOCH, 3, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdatePosition();
            }
        });


        return particle;
    }

    public void start() {
        super.start();
        space = new Continuous2D(0.01, width, height);
        area  = new Continuous2D(0.04, width, height); // radioactive particles

        radiationParticles = new RadiationParticle[numRadioactiveParticles];
        for (int i = 0; i < numRadioactiveParticles; i++) {
            radiationParticles[i] = (RadiationParticle) initializeParticle(new RadiationParticle(this, "r-"+i));
        }

        insulationParticles = new InsulationParticle[numInsulationParticles];
        for (int i = 0; i < numInsulationParticles; i++) {
            insulationParticles[i] = (InsulationParticle) initializeParticle(new InsulationParticle(this, "i-"+i));
        }
    }

    public static void main(String[] args) {
        doLoop(ChemitaxisSim.class, args);
        System.exit(0);
    }

}
