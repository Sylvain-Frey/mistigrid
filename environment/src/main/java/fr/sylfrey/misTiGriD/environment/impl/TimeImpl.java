/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.environment.impl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.environment.Time;
import fr.sylfrey.misTiGriD.environment.Updatable;

/**
 * This component provides a Time service and performs Updatable scheduling :
 * every Updatable service is detected by this Time and periodically updated
 * according to the Updatable specification.
 * @author syl
 *
 */
@Component(name="Time",immediate=true)
@Provides(specifications={Time.class})
public class TimeImpl implements Time {

	@Validate
	public void start() {		
	}
	
	@Bind(specification="fr.sylfrey.misTiGriD.environment.Updatable",id="updatables",aggregate=true,optional=true)
	public void bind(Updatable updatable) {
		UpdatableWrapper task = new UpdatableWrapper(updatable);
		tasks.put(updatable,task);
		timer.scheduleAtFixedRate(task, 3000, updatable.getPeriod());
	}
	
	@Unbind(specification="fr.sylfrey.misTiGriD.environment.Updatable",id="updatables")
	public void unbind(Updatable updatable) {
		tasks.get(updatable).cancel();
		tasks.remove(updatable);
	}
	
	@Override
	public long dayTime() {
		return System.currentTimeMillis() % dayLength;
	}
	
	@Override
	public long dayLength() {
		return dayLength;
	}
	
	@Invalidate
	public void stop() {
		timer.cancel();
		timer = new Timer();
	}
	
	@Property(name="dayLength",mandatory=true)
	public long dayLength;
	
	public ConcurrentHashMap<Updatable,UpdatableWrapper> tasks = new ConcurrentHashMap<Updatable,UpdatableWrapper>();
	
	private Timer timer = new Timer();
	
	private class UpdatableWrapper extends TimerTask {

		private Updatable updatable;
		
		public UpdatableWrapper(Updatable updatable) {
			super();
			this.updatable = updatable;
		}

		@Override
		public void run() {
			updatable.update();
		}
		
	}

}
