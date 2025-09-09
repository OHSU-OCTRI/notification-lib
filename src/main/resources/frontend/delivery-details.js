import { createApp } from 'vue';
import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';

/**
 * In the controller add:
 *  model.put("pageScripts", new String[] { "notificationlib-vendor.js", "delivery-details.js" });
 *
 * In the mustache view:
 * <div id="delivery_details" data-delivery-details="{{.}}"></div>
 */

const rootNode = document.getElementById('delivery_details');
const dataset = rootNode.dataset;

if (rootNode && dataset.deliveryDetails) {
  try {
    const jsonData = JSON.parse(dataset.deliveryDetails);
    createApp(VueJsonPretty, {
      data: jsonData
    }).mount(rootNode);
  } catch (e) {
    console.warn(
      'Delivery details could not be parsed. Falling back to displaying as a string.'
    );
    rootNode.textContent = dataset.deliveryDetails;
  }
}
