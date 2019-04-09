import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	private enum status {
		THINKING,
		HUNGRY,
		EATING,
		EUREKA,
		TALKING,
		HASRIGHTSTICK,
		SLEEPY,
		SLEEPING,
		BULLSHIT
	}
	private Lock lock = new ReentrantLock();
	private status[] state;
	private Condition[] self;
	private int nbPhil;
	private int nbFreeShakers = 2;

	/**
	 * Constructor
	 */
	public Monitor(int nbPhil)
	{
		this.nbPhil = nbPhil;
		this.state = new status[nbPhil];
		this.self = new Condition[nbPhil];
		System.out.println("in the constructor!!");
		for(int i = 0; i < state.length; i++){
			this.state[i] = status.BULLSHIT;
			this.self[i] = lock.newCondition();
		}
		// TODO: set appropriate number of chopsticks based on the # of philosophers
	}



	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public void pickUp(final int id) throws InterruptedException//TASK 2 only eat if you have both forks
	{
		lock.lock();
		state[id] = status.HUNGRY;
		testEat(id);
		if(state[id] != status.EATING){
			System.out.println("Philosopher " + id + " has is waiting to eat");
			self[id].await();
		}
		lock.unlock();
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public void putDown(final int id) //TASK 2 release your neighbors
	{
		lock.lock();
		state[id] = status.THINKING;
		nbFreeShakers++;
		for(int i = 0; i < state.length; i++){
			if(state[i] == status.HUNGRY){
				testEat(i);
			}
		}
		testEat(getLeft(id));
		testEat(getRight(id));
		lock.unlock();
	}

	public void testEat(int id){  //Task 2
		lock.lock();
		if (bothChopsticksFree(id) &&
			(state[id] == status.HUNGRY || state[id] == status.HASRIGHTSTICK) &&
			availableShakers())
		{
			System.out.println("Philosopher " + id + " has passes test eat");
			state[id] = status.EATING;
			nbFreeShakers--;
			self[id].signal(); //>>>>>>>>
		}
		else if (allowedToTakeOneChopstick(id) && rightChopstickIsFree(id) &&  state[id] == status.HUNGRY)
		{
			System.out.println("Philosopher " + id + " has claimed right chopstick");
			state[id] = status.HASRIGHTSTICK;
		}
		lock.unlock();
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public void requestTalk(int id) throws InterruptedException//TASK 2
	{
		lock.lock();
		state[id] = status.EUREKA;
		testTalk(id);
		if(state[id] != status.TALKING){
			self[id].await();
		}
		lock.unlock();
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public void endTalk(int id) //Task 2
	{
		lock.lock();
		state[id] = status.THINKING;
		if(philosopherWantsToTalk())
		{
			signalSomeoneToTalk();
		}
		else
		{
			signalAllToSleep();
		}
		lock.unlock();
	}

	public void testTalk(int id){ //Task 2
		lock.lock();
		if(noOneisSleeping())
		{
			System.out.println("no one is sleeping");
		}
		if(noOneIsTalking())
		{
			System.out.println("no one is talking");
			//printAllStates();
		}
		if(noOneIsTalking() && state[id] == status.EUREKA && noOneisSleeping()){
			state[id] = status.TALKING;
			System.out.println(id+" is talking");
			//printAllStates();
			self[id].signal();
		}
		else if(!noOneisSleeping())
		{
			System.out.println("Someone is SLEEPING, "+ id +" will wait to talk");
		}

		lock.unlock();
	}

	public void requestSleep(int id)
	{
		lock.lock();
		try {
			state[id] = status.SLEEPY;
			testSleep(id);
			if (state[id] != status.SLEEPING) {
				System.out.println("Someone is taking,"+ id+" will wait to sleep");
				self[id].await();
			}
		}
		catch(InterruptedException e)
		{
			System.err.println("Monitor.requestTalk():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
		finally
		{
			lock.unlock();
		}
	}

	public void endSleep(int id)
	{
		lock.lock();
		state[id] = status.THINKING;
		if(noOneisSleeping())
		{
			signalSomeoneToTalk();
		}
		lock.unlock();
	}

	private void testSleep(int id)
	{
		lock.lock();
		if(noOneIsTalking() && state[id] == status.SLEEPY)
		{
			System.out.println("no one is talking, " + id + " will sleep");
			state[id] = status.SLEEPING;
			self[id].signal();
		}
		else if(!availableShakers())
		{
			System.out.println(id +" tries, no shakers");
		}
		lock.unlock();
	}

	private boolean availableShakers(){
		return nbFreeShakers > 0;
	}

	private boolean rightChopstickIsFree(int id){
		return state[getRight(id)] != status.EATING;
	}

	private boolean allowedToTakeOneChopstick(int id){
		return id % 2 == 0;
	}

	private boolean bothChopsticksFree(int id){ //Task 2
		return state[getLeft(id)] != status.EATING &&
				state[getLeft(id)] != status.HASRIGHTSTICK &&
				state[getRight(id)] != status.EATING;
	}

	private void signalSomeoneToTalk() {
		for (int i = 0; i < state.length; i++) {
			if (state[i] == status.EUREKA) {
				testTalk(i);
				return;
			}
		}
	}

	private boolean philosopherWantsToTalk()
	{
		for(int i = 0; i < state.length; i++){
			if(state[i] == status.EUREKA){
				return true;
			}
		}
		return false;
	}

	private boolean noOneIsTalking(){ //Task 2
		for (int i = 0; i < state.length; i++){
			if(state[i] == status.TALKING){
				//System.out.println(i + " is talking!!!!");
				return false;
			}
		}
		return true;
	}

	private void signalAllToSleep(){ //Task 2
		lock.lock();
		for (int i = 0; i < state.length; i++){
			if(state[i] == status.SLEEPY){
				self[i].signal();
			}
		}
		lock.unlock();
	}

	private boolean noOneisSleeping(){ //Task 2
		for (int i = 0; i < state.length; i++){
			if(state[i] == status.SLEEPING){

				return false;
			}
		}
		return true;
	}

	private void printAllStates(){
		for (int i = 0; i < state.length; i++) {
			System.out.println(state[i]);
		}
	}

	private int getRight(int id){
		return (id < nbPhil -1) ? id + 1: 0;
	}

	private int getLeft(int id){
		return (id > 0) ? id - 1 : nbPhil - 1;
	}

}

// EOF
