package com.maxdrob.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class Call {
    int status = 0; // Уровень сотрудника, миниально необходимый для того, чтобы принять звонок

    public void reply(String message) {
        // тут играет (не)приятная музычка, записанная на микрофон от наушников с Алиэкспресс за $0,95
    }

    public void disconnect() {
        reply("Спасибо за звонок!");
    }
}

class Employee {
    CallReceiver CallReciever;
    int status; // Статус оператора: 0 - низшее звено, 1 - тимлид, 2 - БОСС
    boolean free;

    public Employee(int status) {
        this.status = status;
    }

    // Начало разговора
    void ReceiveCall(Call call) {
        free = false;
    }

    // Завершение звонка, если проблема клиента устранена
    void CallHandled(Call call) {
        call.disconnect();
        free = true;

    }

    // Звонок не принят, перевести на вышестоящего

    void CannotHandle(Call call) {
        call.status = status + 1;
        CallReciever.dispatchCall(call);
        free = true;

    }
}


class Manager extends Employee {
    public Manager() {
        super(0);
    }

    public Manager(int status) {
        super(status);
    }
}

class TeamLeader extends Employee {
    public TeamLeader() {
        super(2);
    }
}
class Boss extends Employee {
    public Boss() {
        super(1);
    }
}

class CallReceiver {
    static final int LEVELS = 3; // Три градации участников
    static final int NUM_managers = 4; // 4 оператора звонков низшего звена

    ArrayList<Employee>[] employeeLevels = new ArrayList[LEVELS];

    // Очередь входящих звонков
    Queue<Call>[] callQueues = new LinkedList[LEVELS];

    public CallReceiver() {
        // Содаем операторов низшего звена

        ArrayList<Employee> managers = new ArrayList(NUM_managers);
        for (int k = 0; k < NUM_managers - 1; k++) {
            managers.add(new Manager());
        }
        employeeLevels[0] = managers;

        // Создаем Тимлида

        ArrayList<Employee> teamlead = new ArrayList(1);
        teamlead.add(new TeamLeader());
        employeeLevels[1] = teamlead;

        // Сооздаем Начальника

        ArrayList<Employee> theboss = new ArrayList(1);
        theboss.add(new Boss());
        employeeLevels[1] = theboss;
    }

    Employee getCallReciever(Call call) {
        for (int level = call.status; level < LEVELS - 1; level++) {
            ArrayList<Employee> employeeLevel = employeeLevels[level];
            for (Employee emp : employeeLevel) {
                if (emp.free) {
                    return emp;
                }
            }
        }
        return null;
    }

    // Адресация входящего звонка свободному сотруднику, или перевод на другого ператора

    void dispatchCall(Call call) {

        Employee emp = getCallReciever(call);
        if (emp != null) {
            emp.ReceiveCall(call);
        } else {
            // Помещаем звонок в очередь согласно градации статусов операторов
            call.reply("Пожалуйста дождитесь ответа оператора, ваш звонок АБСОЛЮТНО НАМ НЕВАЖЕН");
            callQueues[call.status].add(call);
        }
    }

    // Сотрудник освободился и готов принять следующий звонок

    void getNextCall(Employee emp) {

        for (int status = emp.status; status >= 0; status--) {
            Queue<Call> que = callQueues[status];
            Call call = que.poll();
            if (call != null) {
                emp.ReceiveCall(call);
                return;
            }
        }
    }
}
