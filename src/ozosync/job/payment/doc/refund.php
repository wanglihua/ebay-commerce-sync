// ### Refund object
$refund = new Refund();
$refund->setAmount($amt);
// ###Sale
// A sale transaction.
// Create a Sale object with the
// given sale transaction id.
$sale = new Sale();
$sale->setId($saleId);
try {
    // Create a new apiContext object so we send a new
    // PayPal-Request-Id (idempotency) header for this resource
    $apiContext = getApiContext($clientId, $clientSecret);
    // Refund the sale
    // (See bootstrap.php for more on `ApiContext`)
    $refundedSale = $sale->refund($refund, $apiContext);
} catch (Exception $ex) {
    ResultPrinter::printError("Refund Sale", "Sale", $refundedSale->getId(), $refund, $ex);
    exit(1);
}
