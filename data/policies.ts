type Section = { title: string; text: string[]; links?: { label: string; href: string }[] }
type Page = { title: string; intro: string; sections: Section[] }
type BilingualPage = { en: Page; zh: Page }
const section = (title: string, ...text: string[]): Section => ({ title, text })
const contactEn = 'Contact 1479030891@qq.com or +86 13310853090. Include your order number and a description of the request. Our company office is not a returns warehouse; obtain return instructions before sending a parcel.'
const contactZh = '请联系 1479030891@qq.com 或 +86 13310853090，并提供订单号及问题说明。公司联系地址不是退货仓库，请先取得退货指引再寄回商品。'
const rightsEn = 'These policies do not restrict mandatory rights available under the laws applicable to your purchase.'
const rightsZh = '本政策不限制适用法律赋予消费者的法定权利。'
export const policies: Record<string, BilingualPage> = {
  'shipping-policy': {
    en: { title: 'Shipping policy', intro: 'Clear delivery information for the United States, Canada and mainland China.', sections: [
      section('Where we deliver', 'We deliver to the United States, Canada and mainland China. Only available in-stock products can be ordered. We currently do not accept pre-orders or backorders. Delivery to PO boxes or addresses a carrier cannot service must be confirmed before ordering.'),
      section('Processing and transit times', 'In-stock orders are dispatched within 1–3 business days after payment confirmation and complete address details are received.', 'Estimated transit after dispatch: United States and Canada, 5–10 business days; mainland China, 3–7 business days. Business days exclude weekends and public holidays. Processing and transit are separate periods. Customs, remote locations and carrier disruptions may extend delivery.'),
      section('Shipping fees and import charges', 'Standard shipping is free. Displayed prices include taxes collected by CBJJ; no additional flat sales-tax surcharge is added. Customs duties, import taxes or clearance charges assessed by a destination authority are the buyer’s responsibility where applicable.'),
      section('Tracking, delays and cancellation', 'Tracking information is provided when an order is dispatched. Contact us if tracking stops updating, delivery is late or a parcel arrives damaged. Keep packaging and photographs of visible damage.', 'If dispatch cannot meet the stated period, we will contact you with a revised estimate and offer cancellation with a full refund for unshipped goods. Unavailable products cannot be ordered as pre-sales.'),
      section('Address accuracy and support', 'Check the country, recipient name, telephone number, postal code and street address before paying. Contact us promptly to request an address change before dispatch.', contactEn, rightsEn),
    ]},
    zh: { title: '配送政策', intro: '面向美国、加拿大及中国大陆的配送与费用说明。', sections: [
      section('配送地区与现货销售', '目前配送范围为美国、加拿大及中国大陆，仅接受有库存商品订单，不提供预售或缺货预订。邮政信箱及承运商无法服务的地址，请在下单前与客服确认。'),
      section('发货及运输时间', '付款确认且收货资料完整后，现货订单在 1–3 个工作日内发货。', '发货后预计运输时间：美国和加拿大 5–10 个工作日，中国大陆 3–7 个工作日。工作日不含周末及公共假期，发货处理时间与运输时间分别计算。清关、偏远地区或承运商异常可能延长送达时间。'),
      section('运费、税费与进口费用', '标准配送免运费，网站售价包含 CBJJ 收取的税费，不额外加收固定销售税。目的地有关部门征收的进口关税、进口税或清关费用，如适用，由买家承担。'),
      section('物流追踪、延迟及取消', '订单发货后提供物流追踪信息。物流停滞、超时或运输破损，请联系客服并保留包装及破损照片。', '如无法在公布的处理时间内发货，我们会告知新的预计时间，并提供未发货商品取消及全额退款的选择；缺货商品不转为预售。'),
      section('收货资料与支持', '付款前请核对收货国家、姓名、联系电话、邮编及详细地址。发货前如需修改地址，请尽快联系客服。', contactZh, rightsZh),
    ]},
  },
  'refund-policy': {
    en: { title: 'Returns & refunds', intro: 'A 30-day return request period, with clear instructions and cost responsibilities.', sections: [
      section('Requesting a return', 'Contact us within 30 calendar days after delivery. Provide the order number, item, reason and relevant photographs. We will provide the return destination and instructions. Do not send a return to our company office without authorization.'),
      section('Change-of-mind returns', 'For a non-defective return, keep the product complete and in resalable condition with accessories and packaging. The buyer pays return shipping. We do not impose an additional restocking fee. Statutory cancellation or return rights remain applicable.'),
      section('Damaged, incorrect or defective goods', 'Contact us with photographs and an explanation. CBJJ covers return shipping for confirmed quality problems, wrong items or transit damage. We will agree a repair, replacement or refund with you. Do not ship a damaged lithium battery without our carrier instructions.'),
      section('Refund timing and currency', 'After the returned goods are received and checked, we initiate an approved refund within five business days, to the original payment method and in the original order currency. Your payment provider controls the final posting time. A completed order’s pricing exchange rate is not recalculated for a refund.'),
      section('Before dispatch', 'Contact us to cancel an unshipped order. An order that has already been dispatched follows the returns process. For a shipping delay, you may choose cancellation and a full refund for unshipped goods.', contactEn, rightsEn),
    ]},
    zh: { title: '退换货与退款政策', intro: '签收后 30 天内可申请退货，明确操作流程及费用承担。', sections: [
      section('申请退货', '请于签收后 30 个自然日内联系 CBJJ，提供订单号、商品名称、退货原因及相关照片。客服将提供退货地址和寄回指引。请勿未经确认寄至公司联系地址。'),
      section('非质量问题退货', '商品应完整并保持可再售状态，配件及包装齐全；退货运费由买家承担，不额外收取补货费。适用法律规定的撤销及退货权利仍然有效。'),
      section('破损、错发及质量问题', '请提供照片及问题说明。经确认的质量问题、错发或运输破损，由 CBJJ 承担退货运费，并与您协商维修、换货或退款。损坏的锂电池请勿自行寄送，应遵循客服及承运商的运输指引。'),
      section('退款时间与币种', '收到退回商品并核验后，于五个工作日内发起经批准的退款，按原订单币种退回原支付渠道。实际到账时间由银行或支付服务商决定。退款不按新的定价汇率重新计算。'),
      section('发货前取消', '未发货订单可联系客服申请取消；已发货订单按退货流程办理。因发货延迟，您可选择取消未发货商品并获得全额退款。', contactZh, rightsZh),
    ]},
  },
  warranty: {
    en: { title: 'Limited warranty', intro: 'One year of limited warranty support for CBJJ products.', sections: [
      section('Coverage', 'CBJJ provides a one-year limited warranty from delivery for defects in materials or workmanship under normal use. Keep your order confirmation and product details. Contact us before sending a product for service.'),
      section('What is excluded', 'Normal wear, consumables, misuse, accidents, unauthorized modifications or repairs, improper charging or storage, and damage from water exposure beyond the product’s documented protection are excluded. A battery’s normal capacity decrease with use is not by itself a manufacturing defect.'),
      section('Making a claim', 'Provide your order number, product model, proof of purchase and photographs or a short video describing the fault. We assess the issue and arrange an appropriate repair, replacement or other remedy. CBJJ covers approved shipping costs for confirmed covered defects.'),
      section('Care and safe use', 'Follow the supplied manual, use a compatible charger, inspect brakes and fasteners, and observe local riding rules. Never charge a visibly damaged battery or leave charging equipment unattended.', contactEn, rightsEn),
    ]},
    zh: { title: '有限保修政策', intro: 'CBJJ 商品提供自签收日起一年的有限保修支持。', sections: [
      section('保修范围', '正常使用情况下因材料或制造工艺引起的缺陷，提供一年有限保修。请保留订单确认及商品资料，寄送维修前先联系客服。'),
      section('不在保修范围的情况', '正常损耗、易耗品、误用、事故、未经授权的改装或维修、不当充电及储存，以及超出商品已说明防护能力的进水损坏，不在有限保修范围。电池随正常使用产生的容量衰减，本身不等同于制造缺陷。'),
      section('申请保修', '请提供订单号、型号、购买凭证，以及描述故障的照片或短视频。客服评估后安排适当的维修、换货或其他处理；经确认属于保修范围的缺陷，由 CBJJ 承担经批准的运输费用。'),
      section('保养与安全使用', '请遵循随附说明书，使用适配充电器，定期检查制动及紧固件，并遵守当地骑行规定。请勿为明显损坏的电池充电，充电时应保持看护。', contactZh, rightsZh),
    ]},
  },
  'payment-methods': {
    en: { title: 'Payment methods', intro: 'Prices, payment availability and order totals are shown before you pay.', sections: [
      section('USD and CNY pricing', 'Orders delivered to the United States or Canada use USD. Mainland China orders use CNY. Language and sales market are separate choices. CBJJ uses a fixed commercial pricing rate configured by the merchant, rather than a live foreign-exchange quote. The final currency and amount are shown in checkout and stored with your order.'),
      section('Available payment channels', 'Checkout enables only a configured payment channel that supports the order currency. If no channel is available, the page states that online payment is temporarily unavailable. Card, wallet or payment-provider badges are informational and do not guarantee checkout availability. We do not collect card numbers on this website.'),
      section('Klarna', 'Klarna is not yet enabled for payment at CBJJ. Our Klarna page explains the service and links to Klarna’s official support and privacy information. Payment options and eligibility depend on country and Klarna’s approval.'),
      section('Confirmation and refunds', 'A pending order is not proof of payment. We confirm payment only after verification by the payment provider. Refunds follow the returns policy and use the original payment method and currency.', contactEn),
    ]},
    zh: { title: '支付方式', intro: '付款前展示订单币种、最终金额及实际可用的支付渠道。', sections: [
      section('美元及人民币定价', '配送至美国或加拿大的订单使用 USD，中国大陆订单使用 CNY。语言与销售地区独立选择。CBJJ 使用商户配置的固定商业定价汇率，并非实时外汇报价；结算页展示最终币种及金额，订单保存对应快照。'),
      section('实际可用渠道', '结算仅启用已配置且支持订单币种的支付渠道。无可用渠道时，页面提示暂不可在线付款。银行卡、钱包及支付服务商标识仅作信息展示，不代表结算时已开通；本网站不收集银行卡号。'),
      section('Klarna', 'CBJJ 尚未开通 Klarna 收款。Klarna 页面提供服务介绍、官方客服及隐私信息入口，具体产品和资格以所在地区及 Klarna 审核结果为准。'),
      section('支付确认及退款', '待付款订单不代表已收款，只有支付服务商结果经验证后才确认支付成功。退款按退换货政策办理，使用原支付渠道及币种。', contactZh),
    ]},
  },
  klarna: {
    en: { title: 'About Klarna', intro: 'Learn about Klarna’s payment services and find official help.', sections: [
      section('What is Klarna?', 'Klarna offers shopping and payment services, including paying immediately or paying over time where offered. Options, fees, terms and approval depend on the customer’s country and the particular service. CBJJ has not yet enabled Klarna at checkout; this page provides information only.'),
      { title: 'Official information and customer service', text: ['Use Klarna’s own website for eligibility, product terms, payment questions and account help. Choose the country appropriate to your residence.'], links: [
        { label: 'About Klarna (United States)', href: 'https://www.klarna.com/us/what-is-klarna/' },
        { label: 'Klarna customer service (United States)', href: 'https://www.klarna.com/us/customer-service/' },
        { label: 'Klarna customer service (Canada)', href: 'https://www.klarna.com/ca/help/' },
      ]},
      { title: 'Klarna privacy information', text: ['Klarna handles information under its own privacy terms when its services are used. Our website currently does not submit checkout data to Klarna. Mainland China visitors should consult the international country selector; this does not imply Klarna is available for mainland China purchases.'], links: [
        { label: 'United States privacy information', href: 'https://www.klarna.com/us/privacy/' },
        { label: 'Canada privacy information', href: 'https://www.klarna.com/ca/privacy/' },
        { label: 'International privacy policy country selector', href: 'https://www.klarna.com/international/privacy-policy/' },
      ]},
      section('Order and product support', 'CBJJ handles its own product, shipping, return and warranty questions. Klarna handles questions about its own accounts and payment services.', contactEn),
    ]},
    zh: { title: '关于 Klarna', intro: '了解 Klarna 的支付服务，并访问官方帮助及隐私信息。', sections: [
      section('Klarna 是什么？', 'Klarna 提供购物及支付服务，包括适用地区的即时付款或分期付款。支付产品、费用、条款和批准资格取决于客户所在国家及所选服务。CBJJ 尚未在结算中启用 Klarna，本页面仅提供信息。'),
      { title: '官方介绍与客服', text: ['有关使用资格、产品条款、付款及账户问题，请访问 Klarna 官方网站并选择实际居住国家。'], links: [
        { label: 'Klarna 官方介绍（美国）', href: 'https://www.klarna.com/us/what-is-klarna/' },
        { label: 'Klarna 官方客服（美国）', href: 'https://www.klarna.com/us/customer-service/' },
        { label: 'Klarna 官方客服（加拿大）', href: 'https://www.klarna.com/ca/help/' },
      ]},
      { title: 'Klarna 隐私信息', text: ['使用 Klarna 服务时，Klarna 按自己的隐私条款处理信息。本网站目前不会向 Klarna 提交结算数据。中国大陆访客可查看全球国家选择入口，该入口不代表 Klarna 已支持中国大陆购买。'], links: [
        { label: '美国 Klarna 隐私信息', href: 'https://www.klarna.com/us/privacy/' },
        { label: '加拿大 Klarna 隐私信息', href: 'https://www.klarna.com/ca/privacy/' },
        { label: '全球 Klarna 隐私政策国家选择入口', href: 'https://www.klarna.com/international/privacy-policy/' },
      ]},
      section('商品与订单问题', 'CBJJ 负责自身商品、配送、退换货及保修问题；Klarna 负责其账户及支付服务问题。', contactZh),
    ]},
  },
  'privacy-policy': {
    en: { title: 'Privacy policy', intro: 'How CBJJ uses information to operate this store and support your purchase.', sections: [
      section('Who operates this store', 'CBJJ is operated by 香港塗柚科技有限公司. Contact 1479030891@qq.com for privacy questions. Registered address: Room 22,11/F,China United Plaza,1008 Tai Nan West Street,Lai Chi Kok,Kowloon,Hong Kong.'),
      section('Information we collect and why', 'We process account details, email address, password hash, shipping addresses, telephone numbers, cart selections, recently viewed products, orders, payment-provider references and customer-submitted reviews. These support account access, checkout, delivery, fraud prevention and after-sales service. If you email us, we use your message and attachments to respond. We do not collect card numbers or Klarna credit application information on this website.'),
      section('Cookies and local preferences', 'Necessary cookies maintain login and your language and sales-market choices. Browser storage may remember recently viewed products. The store does not require advertising cookies for checkout. You can clear browser storage or change preferences; removing login cookies signs you out.'),
      section('Sharing and international processing', 'Where necessary, information is shared with hosting providers, an enabled payment provider, delivery carriers and service partners to fulfill the purchase, and authorities when legally required. The merchant is based in Hong Kong and this store is hosted on infrastructure in the United States, so information can be processed outside your country. Applicable consent and transfer requirements remain in effect. Klarna information is governed by Klarna’s own terms if its services are used; it is not enabled at this store.'),
      section('Retention, protection and your rights', 'We retain information needed for your account, order fulfillment, warranty, accounting and legal obligations, and delete or anonymize it when no longer necessary. Access is restricted to operational needs. You can request access, correction, a copy, deletion or withdrawal of consent where applicable by emailing us. We may need to verify identity, and mandatory record-retention requirements may limit deletion. You may also contact your applicable privacy authority.'),
      { title: 'Klarna privacy information', text: ['Consult the policy appropriate to your residence.'], links: [{ label: 'Klarna international privacy policies', href: 'https://www.klarna.com/international/privacy-policy/' }, { label: 'Klarna US privacy', href: 'https://www.klarna.com/us/privacy/' }, { label: 'Klarna Canada privacy', href: 'https://www.klarna.com/ca/privacy/' }] },
      section('Children and policy updates', 'This store’s purchasing accounts are intended for adults. Do not submit a child’s personal information in reviews or support attachments. We publish material policy changes here with an updated effective date.'),
    ]},
    zh: { title: '隐私政策', intro: 'CBJJ 如何处理商店运营、购买及售后服务所需的信息。', sections: [
      section('运营主体', 'CBJJ 由香港塗柚科技有限公司运营。隐私问题请联系 1479030891@qq.com。联系地址：香港九龍荔枝角大南西街1008號華匯廣場11樓22室。'),
      section('收集的信息及用途', '我们处理账户资料、邮箱、密码哈希、收货地址、电话、购物车选项、最近浏览商品、订单、支付服务商交易标识和用户提交的评价，用于账户登录、结算、配送、防欺诈及售后服务。您发送的邮件及附件用于回复请求。本网站不收集银行卡号或 Klarna 信贷申请资料。'),
      section('Cookie 与偏好设置', '必要 Cookie 用于保持登录状态、语言及销售地区选择；浏览器存储可保存最近浏览的商品。结算不要求启用广告 Cookie。您可清除存储或调整偏好，删除登录 Cookie 会退出账户。'),
      section('信息共享与境外处理', '为履行购买交易，必要信息可提供给托管服务商、已启用的支付服务商、物流承运商及服务合作方，并在法律要求时提供给有关部门。经营主体位于香港，本商店使用美国服务器基础设施，因此信息可能在您所在国家或地区以外处理；适用的同意和跨境处理要求仍然有效。本商店未启用 Klarna，使用其服务时适用其独立隐私条款。'),
      section('保存、保护及您的权利', '信息按账户使用、订单履行、保修、会计及法定义务需要保存，不再需要时删除或匿名化；访问权限限于必要运营用途。您可通过邮箱申请查阅、更正、复制、删除信息或在适用情况下撤回同意。处理请求可能需要验证身份，法定保存义务可能限制删除；您也可向适用的隐私监管机构提出请求或投诉。'),
      { title: 'Klarna 隐私信息', text: ['请按实际居住国家查看对应隐私信息。'], links: [{ label: 'Klarna 全球隐私政策入口', href: 'https://www.klarna.com/international/privacy-policy/' }, { label: 'Klarna 美国隐私信息', href: 'https://www.klarna.com/us/privacy/' }, { label: 'Klarna 加拿大隐私信息', href: 'https://www.klarna.com/ca/privacy/' }] },
      section('儿童及政策更新', '本商店购买账户面向成年人。请勿在评价或客服附件中提交儿童个人信息。重要政策变更将在本页面公布并更新生效日期。'),
    ]},
  },
  'terms-of-service': {
    en: { title: 'Terms of service', intro: 'Terms for using the CBJJ store and placing an order.', sections: [
      section('Merchant and eligibility', 'This store is operated by 香港塗柚科技有限公司 under the CBJJ brand. You must be an adult with legal capacity to make a purchase, or act with legally valid authorization. Provide accurate account and delivery information.'),
      section('Products and orders', 'Product specifications describe the listed model and selected variant. Product showcase images may be generated or edited illustrations; they are not evidence of certification or inventory. Performance depends on load, terrain, weather and use. Do not assume road legality or certification not expressly documented. Order creation is not payment confirmation. We will contact you about a material listing error or inability to fulfill an order and offer cancellation and a refund for paid, unfulfilled goods.'),
      section('Prices and payment', 'USD applies to US and Canadian delivery, CNY to mainland China. Prices include merchant-collected taxes and standard shipping. Potential destination import charges are separate. The checkout total and currency control the purchase; language switching does not change an existing order’s currency. Only enabled, supported payment channels can be used.'),
      section('Delivery and after-sales service', 'The shipping, returns and limited warranty policies explain processing, delivery estimates, cancellations, refunds and support. Applicable mandatory consumer rights take precedence over conflicting terms.'),
      section('Responsible use and store content', 'Follow the supplied product manual and applicable local traffic rules. Do not misuse the site, access other users’ accounts, submit unlawful reviews or attempt to manipulate prices and payment confirmations. Store content may not be copied or used to impersonate CBJJ.'),
      section('Questions and disputes', 'Contact our support team first so we can investigate and resolve a concern. These terms do not require you to waive rights or remedies available under applicable law.', contactEn, rightsEn),
    ]},
    zh: { title: '服务条款', intro: '使用 CBJJ 商店及购买商品的基本约定。', sections: [
      section('商户及购买资格', '本商店由香港塗柚科技有限公司以 CBJJ 品牌运营。购买者应为具有相应法律行为能力的成年人，或具有合法有效的授权，并提供准确的账户及收货资料。'),
      section('商品与订单', '参数对应所列型号及所选变体；商品展示图可能为生成或编辑的示意图，不作为认证或库存证明。性能受负载、路况、天气及使用方式影响，未明确提供证明的上路资格或认证不可推定。创建订单不等于付款成功。如商品信息出现重大错误或无法履约，我们会联系您，并提供取消及已付款未履约商品的退款选择。'),
      section('价格及支付', '美国及加拿大配送订单使用美元，中国大陆使用人民币。售价包含商户收取的税费及标准配送，目的地可能征收的进口费用另计。购买以结算页金额和币种为准，语言切换不会改变已创建订单的币种；只能使用实际启用且支持该币种的支付渠道。'),
      section('配送与售后', '发货、预计送达、取消、退货、退款及保修分别按对应政策办理。适用的法定消费者权利优先于与其冲突的条款。'),
      section('合理使用', '请遵守商品说明书和当地交通规定，不得滥用网站、访问他人账户、提交违法评价、篡改金额或伪造付款确认，不得复制网站内容冒充 CBJJ。'),
      section('咨询及争议', '遇到问题请先联系客服，便于核实并协商处理。本条款不要求您放弃适用法律规定的权利或救济。', contactZh, rightsZh),
    ]},
  },
  'authorized-sales': {
    en: { title: 'Official sales channels', intro: 'Check who you are buying from and where to get CBJJ support.', sections: [section('Our official store', 'cbjjpower.com is the CBJJ store operated by 香港塗柚科技有限公司. We do not publish an authorized third-party dealer list at present. Contact us to verify a seller before purchasing.'), section('Protect your purchase', 'Check the domain and recipient before paying. We do not ask customers to provide passwords or verification codes. Warranty or support for a third-party purchase must be confirmed with the actual seller; statutory rights remain applicable.'), section('Verification and support', contactEn)]},
    zh: { title: '官方销售渠道说明', intro: '核实购买渠道及 CBJJ 客服联系方式。', sections: [section('官方商店', 'cbjjpower.com 为香港塗柚科技有限公司运营的 CBJJ 商店。目前未公布第三方授权经销商名单，购买前可联系客服核实销售方。'), section('保障购买安全', '付款前核对域名及收款方。客服不会索取账户密码或验证码。第三方购买的售后及保修需向实际销售方确认，适用法定权利仍然有效。'), section('渠道核实及支持', contactZh)]},
  },
  'become-a-dealer': {
    en: { title: 'Become a dealer', intro: 'Discuss a CBJJ distribution partnership with our team.', sections: [section('Send your business proposal', 'Email your company name, business location, website or sales channels, intended market, product interests and estimated purchase volume to 1479030891@qq.com.'), section('Review and agreement', 'We review proposals individually. Pricing, supply terms, territory and any authorization are agreed in writing. Sending an inquiry does not grant dealership status or permission to represent CBJJ.'), section('Contact', contactEn)]},
    zh: { title: '经销合作', intro: '与 CBJJ 团队洽谈商品经销合作。', sections: [section('提交合作资料', '请将公司名称、经营所在地、网站或销售渠道、目标市场、意向商品及预计采购量发送至 1479030891@qq.com。'), section('评估及书面约定', '合作申请逐一评估，价格、供货、区域及品牌使用权限以书面协议为准。发送咨询并不代表已获得经销资格或代表 CBJJ 的授权。'), section('联系', contactZh)]},
  },
  'affiliate-program': {
    en: { title: 'Affiliate partnerships', intro: 'Propose a content or referral collaboration with CBJJ.', sections: [section('Who can inquire', 'Content creators, publishers and community organizers can email their channels, audience markets and proposed collaboration to 1479030891@qq.com.'), section('Commercial terms', 'We currently handle partnership inquiries directly. There is no automatic enrollment, referral tracking portal or published commission schedule. Campaign terms and any compensation require a separate written agreement.'), section('Responsible promotion', 'Promotions must accurately describe the products, disclose relevant commercial relationships and avoid unsupported performance or certification claims.') ]},
    zh: { title: '联盟推广合作', intro: '向 CBJJ 提交内容推广或推荐合作提案。', sections: [section('合作咨询', '内容创作者、媒体及社群组织者可将推广渠道、受众市场及合作提案发送至 1479030891@qq.com。'), section('合作条件', '目前由团队直接处理咨询，未开放自动加入、推荐追踪后台或固定佣金表。推广条件及报酬另行书面约定。'), section('真实推广', '推广内容须准确描述商品，披露适用的商业合作关系，避免未经证实的性能或认证宣传。')]},
  },
}
