/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.time.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.time.Trigger;
import org.kie.api.runtime.Calendars;

public class    IntervalTimer extends BaseTimer
    implements
    Timer,
    Externalizable {
    private Date startTime;
    private Date endTime;
    private int  repeatLimit;
    private long delay;
    private long period;
    
    public IntervalTimer() {
        
    }

    public IntervalTimer(Date startTime,
                         Date endTime,
                         int repeatLimit,
                         long delay,
                         long period) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatLimit = repeatLimit;
        this.delay = delay;
        this.period = period;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( startTime );
        out.writeObject( endTime );
        out.writeInt( repeatLimit );
        out.writeLong( delay );
        out.writeLong( period );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.startTime = (Date) in.readObject();
        this.endTime = (Date) in.readObject();
        this.repeatLimit = in.readInt();
        this.delay = in.readLong();
        this.period = in.readLong();
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public Trigger createTrigger( Activation item, InternalWorkingMemory wm ) {
        long timestamp = wm.getTimerService().getCurrentTime();
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = wm.getCalendars();
        return createTrigger( timestamp, calendarNames, calendars );
    }

    public Trigger createTrigger(long timestamp,
                                 LeftTuple leftTuple,
                                 DefaultJobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 InternalWorkingMemory wm) {
        long timeSinceLastFire = 0;

        if ( jh != null ) {
            IntervalTrigger preTrig = (IntervalTrigger) jh.getTimerJobInstance().getTrigger();
            if (preTrig.getLastFireTime() != null) {
                timeSinceLastFire = timestamp - preTrig.getLastFireTime().getTime();
            }
        }

        // if it is already fired calculate the new delay using the period instead of the delay
        long newDelay = timeSinceLastFire > 0 ? period - timeSinceLastFire : delay;
        if (newDelay < 0) {
            newDelay = 0;
        }

        return new IntervalTrigger( timestamp,
                                    this.startTime,
                                    this.endTime,
                                    this.repeatLimit,
                                    newDelay,
                                    this.period,
                                    calendarNames,
                                    calendars );
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        return new IntervalTrigger( timestamp,
                                    this.startTime,
                                    this.endTime,
                                    this.repeatLimit,
                                    this.delay,
                                    this.period,
                                    calendarNames,
                                    calendars );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (delay ^ (delay >>> 32));
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + repeatLimit;
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        IntervalTimer other = (IntervalTimer) obj;
        if ( delay != other.delay ) return false;
        if ( repeatLimit != other.repeatLimit ) return false;
        if ( endTime == null ) {
            if ( other.endTime != null ) return false;
        } else if ( !endTime.equals( other.endTime ) ) return false;
        if ( period != other.period ) return false;
        if ( startTime == null ) {
            if ( other.startTime != null ) return false;
        } else if ( !startTime.equals( other.startTime ) ) return false;
        return true;
    }

    @Override
    public ConditionalElement clone() {
        return new IntervalTimer(startTime,
                                 endTime,
                                 repeatLimit,
                                 delay,
                                 period);
    }
}
