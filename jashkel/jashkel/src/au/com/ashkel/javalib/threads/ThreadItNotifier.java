/**
 * Title: ThreadItNotifier
 * Description: class ThreadItNotifier represents the subject of interest using the
 * Observer pattern. Observers can register thier interest in the subject and
 * are notified when there are changes or updates. This particular implementation
 * supports two mechamisms for updating observers: 
 * 1) ThreadItNotifier can be subclassed and the observer will receive a refernce to the updated
 * subject. This is along the lines of the full Observer pattern. 
 * 2) ThreadItNotifier can be used to inform observers of changes where a general instance 
 * representing the change is transferred to the observer. This is similar to the 
 * java approach.
 * See the CObserver class for the partner class in this pattern.
 * This class uses critical sections to ensure that there are no concurency issues
 * when modifying the observer lists and when updates are presented to observers.
 *
 * Copyright: Copyright (c) 2008 Ashkel Software 
 * @author Ari Edinburg
 * @version 1.0
 * $Revision: 1.1 $<br>
 * $Date: 2008/12/19 02:38:06 $
 */

package au.com.ashkel.javalib.threads;

import java.util.Iterator;
import java.util.List;

/**
 * Class ThreadItNotifier represents the information being monitored for any changes or 
 * updates. Observers can register interest in the subject and will be notified
 * of updates and changes. 
 */
class ThreadItNotifier
{
  //typedef std::list<CThreadItObserver> ThreadItObserverList;
  // Attributes
  /** m_theObserverList is a list of observers that have registered thier interest
   * in being notified when the subject (information) of interest is changed */
  protected List<ThreadItObserver> m_theObserverList;

  /** 
   * Method ThreadItNotifier is the constructor for the instance. This implementation creates
   * a critical section to ensure that there are no concurrency issues when attaching or
   * detaching or when notifications are sent to interested observers. 
   */
  public ThreadItNotifier()
  {
  } // constructor ThreadItNotifier


  /**
   * Method attach allows an oberver to register it's intent to monitor changes or updates to the 
   * Subject under consideration. While the observer is attached to the subject it will receive
   * update / change notifications. 
   * ptheObserver is a reference to the observer that wishes to register it's intent to monitor 
   * changes to the subject. Note that if the ptheObserver is already in the list then the 
   * ptheObserver observer will not be added again.
   */
  public void attach (ThreadItObserver theObserver) 
  {
    // Add the observer to the list if it is not already in the list.
    boolean isInList = false;

    Iterator<ThreadItObserver> theIterator = m_theObserverList.iterator ();

    // Check and see if the observer is already in the list or not. 
    while (theIterator.hasNext ()) 
    {
      if ((theIterator) == theObserver)
      {
        isInList = true;
        break;
      } // if 
    } // for
    if (!isInList)
    {
      m_theObserverList.add (theObserver);
    } // if 
  } // attach

  /**
   * Method detach allows an oberver to register it's intent to no longer monitor changes or 
   * updates to the Subject under consideration. 
   * ptheObserver is a reference to the observer that wishes to register it's intent to stop
   * monitoring changes to the subject.
   */
  public void detach (ThreadItObserver theObserver)
  {
    boolean isInList = false;
    Iterator<ThreadItObserver> theIterator = m_theObserverList.iterator ();

    // Find and remove all the observers from the list
    do
    {
      isInList = false;
      while (theIterator.hasNext ()) 
      {
        if ((theIterator) == theObserver)
        {
          isInList = true;
          break;
        } // if 
      } // for
      if (isInList)
      {
        m_theObserverList.remove (theIterator);
      } // if 
    } while (isInList);
  } // detach

  /**
   * Method removeObservers removes all observers that have attached to this subject.
   */
  void removeObservers ()
  {
    // Find and remove the observer from the list
    m_theObserverList.clear ();
  } // removeObservers

  /**
   * Method cleanExpiredObservers removes all observers that have expired. 
   */
  public void cleanExpiredObservers ()
  {
    boolean isExpired = false;
    Iterator<ThreadItObserver> theIterator = m_theObserverList.iterator ();

    // Find and remove all the observers from the list
    do
    {
      isExpired = false;
      while (theIterator.hasNext ()) 
      {
        if (((ThreadItObserver) theIterator).isValid () == false)
        {
          isExpired = true;
          break;
        } // if 
      } // for
      if (isExpired)
      {
        detach ((ThreadItObserver)theIterator);
      } // if 
    } while (isExpired);
  } // cleanExpiredObservers

  /**
   * Method notifyOnUpdate is used to notify all interestd observers of updates to this subject.
   * When this method is called observers are notified by the OnUpdate method that they have 
   * implemented being invoked. This method will supply a reference to this subject that has changed.
   * This approach is normally used to provide a reference to instances that extend from ThreadItNotifier.
   * NOTE: as this method is called within a critical section no calls to attach or detach should be
   * made.
   */ 
  public void doNotify ()
  {
    Iterator<ThreadItObserver> theIterator = m_theObserverList.iterator ();

    // Notify all the observers to update themselves
    while (theIterator.hasNext ()) 
    {
      ((ThreadItObserver)theIterator).notify ();
    } // for
  } // notify

  public void notify (Cloneable theObject)
  {
    Iterator<ThreadItObserver> theIterator = m_theObserverList.iterator ();

    // Notify all the observers to update themselves
    while (theIterator.hasNext ()) 
    {
      ((ThreadItObserver)theIterator).notify ();
    } // for
  } // notify

  /**
   * Method getNumberOfObservers returns the number of observers currently interested in updates
   * or changes to the subject.
   */
  public int getNumberOfObservers ()
  {
    return (int)m_theObserverList.size ();
  } // getNumberOfObservers

} // class ThreadItNotifier

