package org.gy.demo.log;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 功能描述：排队自旋锁
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/26 11:12
 */
public class TicketLock {

    private AtomicLong serviceNum = new AtomicLong();//服务号
    private AtomicLong ticketNum = new AtomicLong();//排队号

    public long lock() {
        //首先原子性地获得一个排队号
        long myTicketNum = ticketNum.getAndIncrement();
        // 只要当前服务号不是自己的就不断轮询
        while (serviceNum.get() != myTicketNum) {
        }
        return myTicketNum;
    }

    public void unlock(long myTicketNum) {
        // 只有当前拥有者才能释放锁
        serviceNum.compareAndSet(myTicketNum, myTicketNum + 1);
    }

}
