
# **Lab 4 WebSocket — Project Report**

## **Description of Changes**

In this lab assignment, we were asked to fix and complete the test file for the WebSocket server that implements the ELIZA chatbot. The goal was to verify the WebSocket communication and ensure that the server responded correctly to chat messages.

First, I deleted the `@Disabled` annotation from the `onChat` test to enable it.

Then, I explained why we need the line `size = list.size()`.
This line captures the current number of received messages at that specific moment. Since WebSocket communication is asynchronous, the content of list could change while the test is still running.

After that, I used Assertions.assertTrue instead of Assertions.assertEquals.
Due to the asynchronous nature of WebSockets, we cannot guarantee that the expected message will always appear at a fixed index in the list. Using assertTrue allows us to check if the expected message exists within the received list rather than assuming a strict order.

Then, I completed the test with an additional Assertions.assertEquals to check that the messages received were the expected ones and in the correct order when applicable.

Inside the ComplexClient class, I added logic so that when the client received the greeting message, it immediately sent the message "I am feeling sad" 

Finally, I ran the tests, and they passed successfully, confirming that the WebSocket server behaves as expected.

---

## **Technical Decisions**

* **Use of `assertTrue` instead of `assertEquals`:**
  Because WebSocket communication is asynchronous, small variations in timing can occur between sent and received messages. Using assertTrue avoids false negatives when the exact position of a message in the list may vary.

* **Verification of message order:**
  The first three assertions confirmed that the initial messages — the greeting, the prompt, and the separator — were received in the correct order. This ensured that the communication sequence followed the expected dialogue flow before checking the chatbot’s generated response.

---

## **Learning Outcomes**

Through this lab, I learned how to:

* Write and test asynchronous WebSocket communication in Kotlin.
* Understand the WebSocket lifecycle: connection establishment, message exchange, and closure.
* Analyze and verify real-time message exchanges between client and server.

---

## **AI Disclosure**

### **AI Tools Used**

* ChatGPT

---

### **AI-Assisted Work**

* The AI tool helped me understand the Eliza Code, recall the concepts related to WebSocket communication, and structure the test logic properly.
* The AI provided guidance on how to explain asynchronous behavior and the rationale behind using specific assertions.
* 10 % IA assisted, 90 % original work

---
### **Original Work**

* Implementation of the `onChat` test logic, comments, and assertions was done by me.
* The reasoning behind `size = list.size()` and the test design was understood and explained independently.
* I executed and verified all tests locally to confirm the expected WebSocket interaction.

---

