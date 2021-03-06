/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   Define Student Connection Listener Interface,
 *   This is utilized by TeacherRollCallAction to communicate
 *   with the TeacherRollCallService and inform it that a student has
 *   connected
 *
 * Edit: 10/12/2015
 *   Updated onStudentConnect method to gather student IP
 *
 */

package edu.uco.schambers.classmate.ListenerInterfaces;

import java.net.InetAddress;

public interface OnStudentConnectListener {
    // TODO: use Student object (check with someone on status, may do myself)
    void onStudentConnect(String id, InetAddress ip);
}
