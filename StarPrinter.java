public class StarPrinter {
	
	private AppDatabase database;
    private CustomerModel.Customer customer;
    private RegionModel.Region region;

    private SalesHeaderModel orderHeader;
    private ArrayList<SalesLineModel> orderLineList;

	
    public StarPrinter(AppDatabase database, SalesHeaderModel orderHeader) {
        this.database = database;
        this.orderHeader = orderHeader;
        this.orderLineList = database.getSalesLineList(orderHeader.getDocumentNo());
        this.customer = database.getOneCustomer(orderHeader.getSellToCustomerNo());
        this.region = database.getOneRegion(customer.getRegionCode());
    }
	
	
	//portName: printer device bluetooth name. I was used BT:Star Micronics
	//portSettings: printer connection configuration. I was used mini;l10000
	//Look User's Manual for parameters
    public boolean printReceipt(Context context, String portName, String portSettings) {
        ArrayList<byte[]> list = new ArrayList<byte[]>();

        byte[] outputByteBuffer = null;
        list.add(new byte[]{0x1d, 0x57, (byte) 0x80, 0x31}); // Page Area Setting <GS> <W> nL nH (nL = 128, nH = 1)

        list.add(new byte[]{0x1b, 0x61, 0x01}); // Center Justification <ESC> a n (0 Left, 1 Center, 2 Right)


        list.add(("Company Name\n" + "Information\n" + "Information\n\n").getBytes());

        list.add(new byte[]{0x1b, 0x61, 0x00}); // Left Alignment

        list.add(("Order No: " + orderHeader.getDocumentNo() + "\n").getBytes());
        list.add(("Date: " + orderHeader.getOrderDate() + "\n").getBytes());
        list.add(("Customer: " + customer.getName() + "\n").getBytes());
        list.add(("Region : " + region.getName() + "-" + region.getCode() + "\n").getBytes());
        list.add(("Customer Note: " + customer.getCustomerNote() + "\n").getBytes());
        list.add(("Order Note : " + orderHeader.getComment() + "\n").getBytes());

        list.add(("--------------------------------\n").getBytes());

        list.add(new byte[]{0x1b, 0x45, 0x01}); // Set Emphasized Printing ON

        list.add("Description\n".getBytes());

        list.add(new byte[]{0x1b, 0x45, 0x00}); // Set Emphasized Printing OFF (same command as on)

        list.add(("--------------------------------\n").getBytes());

        double totalPrice = 0.0;
        double totalNetPrice = 0.0;
        double totalQty = 0;
        double totalDiscount = 0.0;
        for (SalesLineModel line : orderLineList) {
            ItemModel.Item item = database.getOneItem(line.getItemNo());
            list.add(new byte[]{0x1b, 0x61, 0x00}); // Left Alignment
            outputByteBuffer = (item.getDescription() + "\n").getBytes();
            list.add(outputByteBuffer);
            outputByteBuffer = (line.getQuantity() + " " + line.getItemUnit()+" - ").getBytes();
            list.add(outputByteBuffer);
            list.add(new byte[]{(byte) 0x9c});// Pound Symbol
            outputByteBuffer = (line.getUnitPrice()).getBytes();
            list.add(outputByteBuffer);
            list.add(new byte[]{0x1b, 0x61, 0x02}); // Right Alignment
            list.add(new byte[]{(byte) 0x9c});// Pound Symbol
            outputByteBuffer = (line.getLineAmountIncludeVat() + "\n").getBytes();
            list.add(outputByteBuffer);
            totalQty += Double.parseDouble(line.getQuantity());
            totalDiscount += Double.parseDouble(line.getLineDiscountAmount());
            totalNetPrice += Double.parseDouble(line.getLineAmount());
            totalPrice += Double.parseDouble(line.getLineAmountIncludeVat());
        }

        list.add(new byte[]{0x1b, 0x45, 0x01}); // Set Emphasized Printing ON

        list.add(("\n--------------------------------\n").getBytes());

        list.add(new byte[]{0x1b, 0x61, 0x02}); // Right Alignment

        list.add(("Total Line: " + orderLineList.size() + "\n").getBytes());
        list.add(("Quantity: " + String.format("%.2f", totalQty) + "\n").getBytes());
        list.add(("Total Net Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});// Pound Symbol
        list.add((String.format("%.2f", totalNetPrice) + "\n").getBytes());
        list.add(("VAT Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});// Pound Symbol
        list.add((String.format("%.2f", (totalPrice-totalNetPrice)) + "\n").getBytes());
        list.add(("Discount Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});// Pound Symbol
        list.add((String.format("%.2f", totalDiscount) + "\n").getBytes());
        list.add(("--------------------------------\n").getBytes());
        list.add(("Total Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});// Pound Symbol
        list.add((String.format("%.2f", totalPrice) + "\n").getBytes());
        list.add(("--------------------------------\n").getBytes());

        list.add(new byte[]{0x1b, 0x45, 0x00}); // Set Emphasized Printing OFF (same command as on)

        list.add(("Sales Person Code: " + orderHeader.getSalesPersonCode() + "\n").getBytes());

        list.add("\n\n\n".getBytes());

        return sendCommand(context, portName, portSettings, list);
    }

        ArrayList<byte[]> list = new ArrayList<byte[]>();

        byte[] outputByteBuffer = null;
        list.add(new byte[]{0x1d, 0x57, (byte) 0x80, 0x31}); // Page Area Setting <GS> <W> nL nH (nL = 128, nH = 1)

        list.add(new byte[]{0x1b, 0x61, 0x01}); // Center Justification <ESC> a n (0 Left, 1 Center, 2 Right)

        // outputByteBuffer = ("[Print Stored Logo Below]\n\n").getBytes();
        // port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
        //
        // list.add(new byte[]{0x1b, 0x66, 0x00}); //Stored Logo Printing <ESC> f n (n = Store Logo # = 0 or 1 or 2 etc.)

        //list.add(("Star Clothing Boutique\n" + "123 Star Road\n" + "City, State 12345\n\n").getBytes());

        list.add(new byte[]{0x1b, 0x61, 0x00}); // Left Alignment

        list.add(("Order No: " + invoiceHeader.getDocumentNo() + "\n").getBytes());
        list.add(("Date: " + invoiceHeader.getOrderDate() + "\n").getBytes());
        list.add(("Customer: " + customer.getName() + "\n").getBytes());
        list.add(("Region : " + region.getName() + "-" + region.getCode() + "\n").getBytes());
        list.add(("Customer Note: " + customer.getCustomerNote() + "\n").getBytes());
        list.add(("Order Note : " + invoiceHeader.getComment() + "\n").getBytes());

        list.add(("--------------------------------\n").getBytes());

        list.add(new byte[]{0x1b, 0x45, 0x01}); // Set Emphasized Printing ON

        list.add("Description\n".getBytes());

        list.add(new byte[]{0x1b, 0x45, 0x00}); // Set Emphasized Printing OFF (same command as on)

        list.add(("--------------------------------\n").getBytes());

        double totalPrice = 0.0;
        double totalNetPrice = 0.0;
        double totalQty = 0;
        double totalDiscount = 0.0;
        for (InvoiceLineModel line : invoiceLineList) {
            ItemModel.Item item = database.getOneItem(line.getItemNo());
            list.add(new byte[]{0x1b, 0x61, 0x00}); // Left Alignment
            outputByteBuffer = (item.getDescription() + "\n").getBytes();
            list.add(outputByteBuffer);
            outputByteBuffer = (line.getQuantity() + " " + line.getItemUnit()+" - ").getBytes();
            list.add(outputByteBuffer);
            list.add(new byte[]{(byte) 0x9c});
            outputByteBuffer = (line.getUnitPrice()).getBytes();
            list.add(outputByteBuffer);
            list.add(new byte[]{0x1b, 0x61, 0x02}); // Right Alignment
            list.add(new byte[]{(byte) 0x9c});
            outputByteBuffer = (line.getLineAmountIncludeVat() + "\n").getBytes();
            list.add(outputByteBuffer);
            totalQty += Double.parseDouble(line.getQuantity());
            totalDiscount += Double.parseDouble(line.getLineDiscountAmount());
            totalNetPrice += Double.parseDouble(line.getLineAmount());
            totalPrice += Double.parseDouble(line.getLineAmountIncludeVat());
        }

        list.add(new byte[]{0x1b, 0x45, 0x01}); // Set Emphasized Printing ON

        list.add(("\n--------------------------------\n").getBytes());

        list.add(new byte[]{0x1b, 0x61, 0x02}); // Right Alignment

        list.add(("Total Line: " + invoiceLineList.size() + "\n").getBytes());
        list.add(("Quantity: " + String.format("%.2f", totalQty) + "\n").getBytes());
        list.add(("Total Net Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});
        list.add((String.format("%.2f", totalNetPrice) + "\n").getBytes());
        list.add(("VAT Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});
        list.add((String.format("%.2f", (totalPrice-totalNetPrice)) + "\n").getBytes());
        list.add(("Discount Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});
        list.add((String.format("%.2f", totalDiscount) + "\n").getBytes());
        list.add(("--------------------------------\n").getBytes());
        list.add(("Total Amount: ").getBytes());
        list.add(new byte[]{(byte) 0x9c});
        list.add((String.format("%.2f", totalPrice) + "\n").getBytes());
        list.add(("--------------------------------\n").getBytes());

        list.add(new byte[]{0x1b, 0x45, 0x00}); // Set Emphasized Printing OFF (same command as on)

        list.add(("Sales Person Code: " + invoiceHeader.getSalesPersonCode() + "\n").getBytes());

        list.add("\n\n\n".getBytes());

        return sendCommand(context, portName, portSettings, list);
    }

    private static boolean sendCommand(Context context, String portName, String portSettings, ArrayList<byte[]> byteList) {
        boolean result = true;
        StarIOPort port = null;
        try {
            /*
             * using StarIOPort3.1.jar (support USB Port) Android OS Version: upper 2.2
             */
            port = StarIOPort.getPort(portName, portSettings, 20000, context);
            /*
             * using StarIOPort.jar Android OS Version: under 2.1 port = StarIOPort.getPort(portName, portSettings, 10000);
             */
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
             * portable(ESC/POS) Printer Firmware Version 2.4 later, SM-S220i(Firmware Version 2.0 later)
             * Using Begin / End Checked Block method for preventing "data detective".
             * When sending large amounts of raster data, use Begin / End Checked Block method and adjust the value in the timeout in the "StarIOPort.getPort" in order to prevent "timeout" of the "endCheckedBlock method" while a printing.
             * If receipt print is success but timeout error occurs(Show message which is "There was no response of the printer within the timeout period."), need to change value of timeout more longer in "StarIOPort.getPort" method. (e.g.) 10000 -> 30000When use "Begin / End Checked Block Sample Code", do comment out "query commands Sample code".
             */

            /* Start of Begin / End Checked Block Sample code */
            StarPrinterStatus status = port.beginCheckedBlock();

            if (true == status.offline) {
                throw new StarIOPortException("A printer is offline");
            }

            byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(byteList);
            port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);

            port.setEndCheckedBlockTimeoutMillis(30000);// Change the timeout time of endCheckedBlock method.
            status = port.endCheckedBlock();

            if (true == status.coverOpen) {
                throw new StarIOPortException("Printer cover is open");
            } else if (true == status.receiptPaperEmpty) {
                throw new StarIOPortException("Receipt paper is empty");
            } else if (true == status.offline) {
                throw new StarIOPortException("Printer is offline");
            }
            /* End of Begin / End Checked Block Sample code */

            /*
             * portable(ESC/POS) Printer Firmware Version 2.3 earlier
             * Using query commands for preventing "data detective".
             * When sending large amounts of raster data, send query commands after writePort data for confirming the end of printing and adjust the value in the timeout in the "checkPrinterSendToComplete" method in order to prevent "timeout" of the "sending query commands" while a printing.
             * If receipt print is success but timeout error occurs(Show message which is "There was no response of the printer within the timeout period."), need to change value of timeout more longer in "checkPrinterSendToComplete" method. (e.g.) 10000 -> 30000When use "query commands Sample code", do comment out "Begin / End Checked Block Sample Code".
             */

            /* Start of query commands Sample code */
            // byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(byteList);
            // port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);
            //
            // checkPrinterSendToComplete(port);
            /* End of query commands Sample code */
        } catch (StarIOPortException e) {
            result = false;
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setNegativeButton("Ok", null);
            AlertDialog alert = dialog.create();
            alert.setTitle("Failure");
            alert.setMessage(e.getMessage());
            alert.setCancelable(false);
            alert.show();
        } finally {
            if (port != null) {
                try {
                    StarIOPort.releasePort(port);
                } catch (StarIOPortException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private static byte[] convertFromListByteArrayTobyteArray(List<byte[]> ByteArray) {
        int dataLength = 0;
        for (int i = 0; i < ByteArray.size(); i++) {
            dataLength += ByteArray.get(i).length;
        }

        int distPosition = 0;
        byte[] byteArray = new byte[dataLength];
        for (int i = 0; i < ByteArray.size(); i++) {
            System.arraycopy(ByteArray.get(i), 0, byteArray, distPosition, ByteArray.get(i).length);
            distPosition += ByteArray.get(i).length;
        }

        return byteArray;
    }
}