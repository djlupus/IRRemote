using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

using System.IO.Ports;
using System.Net.Sockets;
using System.Net;
using System.Threading;




namespace RemoteControl
{
    public partial class Form1 : Form
    {
        private SerialPort comPort = new SerialPort(); //SerialPort object init 
        private String SerialPort = "";

        //Thread
        bool isWorking; //Thread will continue untill this is false.
        Thread t; //Thread

        //UDP Information
        private const int listenPort = 11000; //could be moved to a textfield in the field or moved to a 
                                              //config file to be loaded at run time.

        public Form1()
        {
            InitializeComponent();

            ///
            ///Pulls in all the avaliable serial ports.
            ///

            foreach (string str in SerialPort.GetPortNames())
            {
                comboBox1.Items.Add(str);
                comboBox1.SelectedIndex = 0;
            }

        }


        /// <summary>
        /// Function for listenting incoming UDP connections.
        /// Then will send the data to the selected serial port
        /// </summary>
        private void listen()
        {
            //variables
            string received_data;
            byte[] received_byte_array;
           
                //Set up the UDP Client.
                UdpClient listener = new UdpClient(listenPort);
                IPEndPoint groupEP = new IPEndPoint(IPAddress.Any, listenPort);
               
              
                    
                try
                {
                    //Listens for data and then sends it, using the WriteData(String) function.
                    while (isWorking)
                    {
                        received_byte_array = listener.Receive(ref groupEP);

                        received_data = Encoding.ASCII.GetString(received_byte_array, 0, received_byte_array.Length);

                        if (received_data.Equals("1"))
                        {
                            WriteData("1");
                        }
                        else if (received_data.Equals("2"))
                        {
                            WriteData("2");
                        }
                        else if (received_data.Equals("3"))
                        {
                            WriteData("3");
                        }
                        else if (received_data.Equals("4"))
                        {
                            WriteData("4");
                        }
                        else if (received_data.Equals("5"))
                        {
                            WriteData("5");
                        }
                        else if (received_data.Equals("6"))
                        {
                            WriteData("6");
                        }
                        else if (received_data.Equals("7"))
                        {
                            WriteData("7");
                        }
                        else if (received_data.Equals("8"))
                        {
                            WriteData("8");
                        }
                        else if (received_data.Equals("9"))
                        {
                            WriteData("9");
                        }
                        else if (received_data.Equals("0"))
                        {
                            WriteData("0");
                        }
                    }
                }
                catch (Exception ex)
                {

                }
            
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }
        
        /// <summary>
        /// Opens the serial port.
        /// </summary>
        /// <returns></returns>
        private Boolean OpenPort()
        {

            try
            {
                //check if the port is already open.
                if (comPort.IsOpen == true) comPort.Close();
                comPort.BaudRate = 19200;
                comPort.PortName = SerialPort;
                comPort.DataReceived += new SerialDataReceivedEventHandler(comPort_DataReceived);
                comPort.Open();

                return true;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }

            return false;
        }

        /// <summary>
        /// Function to be used if you want the propeller to send data to you.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        public void comPort_DataReceived(object sender, SerialDataReceivedEventArgs e)
        {
        }


        public void WriteData(string msg)
        {
            //Make sure port is open before writing to the serial port.
            if (!(comPort.IsOpen == true)) OpenPort();

            //send the serial data to the selected serial port
            comPort.Write(msg);
        }
        public void WriteData(byte[] msg)
        {
                   //Make sure port is open before writing to the serial port.
                if(!(comPort.IsOpen == true)) OpenPort();

                comPort.Write(msg, 0, msg.Length); //send the serial data to the selected serial port
        }

        private byte[] HexToByte(string msg)
        {
            msg = msg.Replace(" ", "");
            byte[] comBuffer = new byte[msg.Length / 2];

            for (int i = 0; i < msg.Length; i += 2)
            {
                comBuffer[i / 2] = (byte)Convert.ToByte(msg.Substring(i, 2), 16);
            }

            return comBuffer;
        }

        private string ByteToHex(byte[] comByte)
        {
            //create a new StringBuilder object
            StringBuilder builder = new StringBuilder(comByte.Length * 3);
            //loop through each byte in the array
            foreach (byte data in comByte)
                //convert the byte to a string and add to the stringbuilder
                builder.Append(Convert.ToString(data, 16).PadLeft(2, '0').PadRight(3, ' '));
            //return the converted value
            return builder.ToString().ToUpper();
        }

        /// <summary>
        /// Sends 1
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button1_Click(object sender, EventArgs e)
        {
            //send one.
            WriteData("1");
        }

        /// <summary>
        /// Sends 2 to the serial port
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button2_Click(object sender, EventArgs e)
        {
            WriteData("2");
        }

        /// <summary>
        /// Sends 3
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button3_Click(object sender, EventArgs e)
        {
            WriteData("3");
        }

         /// <summary>
         /// Sends 4
         /// </summary>
         /// <param name="sender"></param>
         /// <param name="e"></param>
        private void button4_Click(object sender, EventArgs e)
        {
            WriteData("4");
        }

        /// <summary>
        /// Sends 5
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button5_Click(object sender, EventArgs e)
        {
            WriteData("5");
        }

        /// <summary>
        /// Sends 6
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button6_Click(object sender, EventArgs e)
        {
            WriteData("6");
        }


        /// <summary>
        /// Sends 7
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button7_Click(object sender, EventArgs e)
        {
            WriteData("7");
        }

        /// <summary>
        /// Sends 8
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button8_Click(object sender, EventArgs e)
        {
            WriteData("8");
        }

        /// <summary>
        /// Sends 9
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button9_Click(object sender, EventArgs e)
        {
            WriteData("9");
        }
        /// <summary>
        /// Sends 0
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button10_Click(object sender, EventArgs e)
        {
            WriteData("0");
        }

       /// <summary>
       /// Tells Thread to stop
       /// </summary>
       /// <param name="sender"></param>
       /// <param name="e"></param>
        private void button12_Click(object sender, EventArgs e)
        {
            isWorking = false;
            t.Abort();
        }

        /// <summary>
        /// Starts Thread to listen for incoming connections and data
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button11_Click_1(object sender, EventArgs e)
        {
            isWorking = true;
            SerialPort = comboBox1.Text;
            t = new Thread(listen);
            t.IsBackground = true;
            t.Start();
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            SerialPort = comboBox1.Text;
        }
    }
}
