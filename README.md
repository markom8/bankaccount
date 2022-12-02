# whitebox
Modeling a Bank Account and its Transactions



In this example, we will have bank accounts, credit lines, and payments (debit and credit payments), and we always want to 
know the actual account balance and whether a pending debit would exceed the overdraft limit of a bank account. 
Besides, we want to be able to retrieve the last payments on a bank account since a given calendar date and list all 
bank accounts actually in the red.



A simple way to do this is to have a booking application with methods to tell when an account gets debited or credited. 
In this case, the application would look for the actual bank account and update the account balance according to the payment. 
The domain model would always represent the current state and maintain the required information.



Introducing Event Sourcing and CQRS (Command and Query Responsibility Segregation) adds a level of indirection to this process 
and decouples the payment event from capturing the actual account balances or other relevant information.



Your task.



Design and implement (in Java) an example program representing the bank account application using Event Sourcing and CQRS.



More concrete.



Users should be able to open a bank account, and each bank account begins with an initial deposit. For simplicity, 
there is no need to consider closing a bank account.
Moreover, each bank account utilizes a credit line granted by the bank to overdraw an account. 
Assume that a given credit line does not change over time.
Each payment might be a credit or debit payment, consisting of a value date and booking amount.
Credit and debit payments affect the bank account by either increasing or decreasing the account balance. 
There is no need to consider overdrafts at this point, i.e., if payment is released, the account balance gets debited or credited accordingly.


Finally, let's define some typical CRUD (Create, Read, Update, and Delete) operations on our domain model:



It should be possible to open a bank account with an initial deposit and credit line. 
It should be possible to retrieve the current account balance of a given bank account.
It should be possible to test if a pending debit payment would exceed the overdraft limit of that bank account.
It should be possible to get a list of all transactions booked of a given bank account since a given calendar date.
It should be possible to receive a list of all bank accounts in the red, i.e., whose account balance is lower than zero.


General remark on the programming exercise.



This sample project intends to be the baseline for the upcoming technical discussion. It should underpin your technical understanding and demonstrate how you set up a project and structure your code. To this end, please keep it simple and don't overcomplicate the application, but we expect to see implemented tests. Moreover, keep topics like readability, design patterns, and code quality in mind. 
