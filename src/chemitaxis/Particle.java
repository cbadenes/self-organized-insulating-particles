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

import org.apache.commons.collections4.queue.CircularFifoQueue;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

import java.util.Iterator;

/**
 * Created by cbadenes on 12/12/14.
 */
public abstract class Particle {

    protected CircularFifoQueue lastMovements;
    protected MutableDouble2D position = new MutableDouble2D();
    protected MutableDouble2D velocity = new MutableDouble2D();

    protected ChemitaxisSim sim;
    protected String id;
    protected int intensity;
    protected double responseRate;

    protected Particle(double x, double y, double vx, double vy, ChemitaxisSim sim, String id, int intensity, double responseRate) {
        this.id = id;
        this.sim = sim;
        this.position.setTo(x, y);
        this.velocity.setTo(vx, vy);
        this.intensity = intensity;
        this.lastMovements = new CircularFifoQueue(3);
        this.responseRate = responseRate;
        sim.space.setObjectLocation(this,new Double2D(position));
    }

    public abstract java.awt.Color getColor();

    public abstract void stepUpdateRadiation();

    public abstract void stepUpdateVelocity();

    public abstract void stepUpdatePosition();

    protected MutableDouble2D limitToMaxVelocity(MutableDouble2D displacement, double max){
        if ((Math.abs(displacement.getY()) < max)
                && (Math.abs(displacement.getX()) < max)) return displacement;

        double absX = Math.abs(displacement.x);
        double absY = Math.abs(displacement.y);

        double valueX = (displacement.x < 0)? -1 : 1;
        double valueY = (displacement.y < 0)? -1 : 1;

        if (absY >= absX){
            valueX *= (absX*max)/absY;
            valueY *= max;

        }else{
            valueY *= (absY*max)/absX;
            valueX *= max;
        }
        return new MutableDouble2D(valueX, valueY);
    }

    protected double distance (MutableDouble2D p1, MutableDouble2D p2){
        // Handle toroidal space
        return Math.sqrt(Math.pow( Math.min( Math.abs(p1.x - p2.x),  sim.space.width - Math.abs(p1.x - p2.x)), 2) +
                                    Math.pow( Math.min( Math.abs(p1.y - p2.y),  sim.space.height - Math.abs(p1.y - p2.y)), 2));
    }

    protected MutableDouble2D randomMovement(){
        return limitToMaxVelocity(
                new MutableDouble2D(
                        (sim.random.nextDouble() * sim.width) - (sim.width * 0.5),
                        (sim.random.nextDouble() * sim.height) - (sim.height * 0.5)
                ), sim.getMaxVelocity()
        );
    }

    protected Double2D calculateDisplacementBy(MutableDouble2D position, double multiplier){
        double x1 = this.position.x;
        double y1 = this.position.y;
        double x2 = position.x;
        double y2 = position.y;
        // Force
        double distance = distance(this.position, position);
        double force = (1 / distance)*multiplier;
        // Toroidal space
        double toroidalX = Math.abs(x2 - x1) < sim.space.width - Math.abs(x2 - x1)? force * (x2 - x1):force * (x1 -x2);
        double toroidalY = Math.abs(y2 - y1) < sim.space.width - Math.abs(y2 - y1)? force * (y2 - y1):force * (y1 -y2);
        return new Double2D(toroidalX,toroidalY);
    }

    protected boolean moveFrom(MutableDouble2D current, double distance){
        if (!this.lastMovements.isEmpty()){
            Iterator iterator = this.lastMovements.iterator();
            while(iterator.hasNext()){
                Double2D point = (Double2D) iterator.next();
                if (distance(new MutableDouble2D(point), current) < distance) return false;
            }
        }
        return true;
    }

}
