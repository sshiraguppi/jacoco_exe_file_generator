package com.intuit.socket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

/**
 * This example connects to a coverage agent that run in output mode
 * <code>tcpserver</code> and requests execution data. The collected data is
 * dumped to a local file.
 */
public final class ExecutionDataClient {

	/**
	 * Starts the execution data request.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main() throws IOException {
		ConfigProperyValues configPropValues = new ConfigProperyValues();
		String checkMultipleIps = configPropValues.getPropValues("ip_address_port");
		String ip_address_port = null;
		String port_number = null;
		String filename = "jacoco";

		String[] ipPortSets = checkMultipleIps.split(",");
		for (int i = 0; i < ipPortSets.length; i++) {
			String ip = ipPortSets[i];
			int index = ip.indexOf(":");
			if (index != -1) {
				final FileOutputStream localFile = new FileOutputStream(
						configPropValues.getPropValues("destfile") + "\\" + filename
								+ (i + 1)+".exec");
				ip_address_port = ip.substring(0, index);
				port_number = ip.substring(index + 1, ip.length());
				final ExecutionDataWriter localWriter = new ExecutionDataWriter(
						localFile);
				// Open a socket to the coverage agent:
				final Socket socket = new Socket(
						InetAddress.getByName(ip_address_port),
						Integer.parseInt(port_number));
				final RemoteControlWriter writer = new RemoteControlWriter(
						socket.getOutputStream());
				final RemoteControlReader reader = new RemoteControlReader(
						socket.getInputStream());
				reader.setSessionInfoVisitor(localWriter);
				reader.setExecutionDataVisitor(localWriter);

				// Send a dump command and read the response:
				writer.visitDumpCommand(true, false);
				reader.read();
				socket.close();
				localFile.close();
			}
		}
	}

	private ExecutionDataClient() {
	}
}