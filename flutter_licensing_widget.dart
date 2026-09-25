// ==============================================================================
// Flutter / Dart Implementation: Content Licensing & Reuse Rights Engine
// File: flutter_licensing_widget.dart
// ==============================================================================

import 'package:flutter/material.dart';

/// ---------------------------------------------------------------------------
/// DATA MODEL: Content Licensing & Reuse Rights Configuration
/// ---------------------------------------------------------------------------
class ContentLicensingConfig {
  final bool permitReuse;
  final bool allowAudioReuse;
  final bool allowVideoRemapping;
  final bool allowMarketplaceShowcase;
  final String licenseBadge;
  final String creatorHandle;
  final DateTime createdAt;

  ContentLicensingConfig({
    this.permitReuse = true,
    this.allowAudioReuse = true,
    this.allowVideoRemapping = true,
    this.allowMarketplaceShowcase = true,
    String? licenseBadge,
    this.creatorHandle = '',
    DateTime? createdAt,
  })  : licenseBadge = licenseBadge ??
            (permitReuse
                ? 'Localiiiy Creative Commons (LCC)'
                : 'All Rights Reserved (ARR)'),
        createdAt = createdAt ?? DateTime.now();

  bool get isCreativeCommons => permitReuse;

  ContentLicensingConfig copyWith({
    bool? permitReuse,
    bool? allowAudioReuse,
    bool? allowVideoRemapping,
    bool? allowMarketplaceShowcase,
    String? licenseBadge,
    String? creatorHandle,
    DateTime? createdAt,
  }) {
    final newPermitReuse = permitReuse ?? this.permitReuse;
    return ContentLicensingConfig(
      permitReuse: newPermitReuse,
      allowAudioReuse: allowAudioReuse ?? this.allowAudioReuse,
      allowVideoRemapping: allowVideoRemapping ?? this.allowVideoRemapping,
      allowMarketplaceShowcase:
          allowMarketplaceShowcase ?? this.allowMarketplaceShowcase,
      licenseBadge: licenseBadge ??
          (newPermitReuse
              ? 'Localiiiy Creative Commons (LCC)'
              : 'All Rights Reserved (ARR)'),
      creatorHandle: creatorHandle ?? this.creatorHandle,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'permit_reuse': permitReuse,
      'allow_audio_reuse': allowAudioReuse,
      'allow_video_remapping': allowVideoRemapping,
      'allow_marketplace_showcase': allowMarketplaceShowcase,
      'license_badge': licenseBadge,
      'creator_handle': creatorHandle,
      'created_at': createdAt.toIso8601String(),
    };
  }

  factory ContentLicensingConfig.fromMap(Map<String, dynamic> map) {
    return ContentLicensingConfig(
      permitReuse: map['permit_reuse'] as bool? ?? true,
      allowAudioReuse: map['allow_audio_reuse'] as bool? ?? true,
      allowVideoRemapping: map['allow_video_remapping'] as bool? ?? true,
      allowMarketplaceShowcase:
          map['allow_marketplace_showcase'] as bool? ?? true,
      licenseBadge: map['license_badge'] as String?,
      creatorHandle: map['creator_handle'] as String? ?? '',
      createdAt: map['created_at'] != null
          ? DateTime.parse(map['created_at'] as String)
          : null,
    );
  }
}

/// ---------------------------------------------------------------------------
/// FLUTTER UI WIDGET: Licensing & Reuse Rights Upload Section
/// ---------------------------------------------------------------------------
class LicensingAndReuseRightsWidget extends StatefulWidget {
  final ContentLicensingConfig initialConfig;
  final ValueChanged<ContentLicensingConfig> onConfigChanged;

  const LicensingAndReuseRightsWidget({
    Key? key,
    required this.initialConfig,
    required this.onConfigChanged,
  }) : super(key: key);

  @override
  State<LicensingAndReuseRightsWidget> createState() =>
      _LicensingAndReuseRightsWidgetState();
}

class _LicensingAndReuseRightsWidgetState
    extends State<LicensingAndReuseRightsWidget> {
  late ContentLicensingConfig _config;

  @override
  void initState() {
    super.initState();
    _config = widget.initialConfig;
  }

  void _updateConfig(ContentLicensingConfig updated) {
    setState(() {
      _config = updated;
    });
    widget.onConfigChanged(updated);
  }

  @override
  Widget build(BuildContext context) {
    final bool isLcc = _config.permitReuse;
    final primaryColor = isLcc ? const Color(0xFF10B981) : const Color(0xFFF43F5E);
    final bgColor = isLcc ? const Color(0xFF041A12) : const Color(0xFF1A0A0A);

    return Container(
      margin: const EdgeInsets.symmetric(vertical: 8.0),
      padding: const EdgeInsets.all(14.0),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(14.0),
        border: Border.all(color: primaryColor.withOpacity(0.55), width: 1.2),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header with Dynamic Badge
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Container(
                    width: 28,
                    height: 28,
                    decoration: BoxDecoration(
                      color: primaryColor.withOpacity(0.2),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(
                      isLcc ? Icons.verified_user : Icons.lock,
                      size: 16,
                      color: primaryColor,
                    ),
                  ),
                  const SizedBox(width: 8),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Licensing & Reuse Rights',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 13,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        _config.licenseBadge,
                        style: TextStyle(
                          color: primaryColor,
                          fontSize: 10,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              // Dynamic Terms Badge Tag
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: primaryColor.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(100),
                  border: Border.all(color: primaryColor, width: 0.8),
                ),
                child: Text(
                  isLcc ? 'LCC BADGE 🛡️' : 'STRICT ARR 🔒',
                  style: TextStyle(
                    color: primaryColor,
                    fontSize: 9.5,
                    fontWeight: FontWeight.bold,
                    fontFamily: 'monospace',
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),

          // Master Switch Toggle Container
          Container(
            padding: const EdgeInsets.all(10.0),
            decoration: BoxDecoration(
              color: Colors.black.withOpacity(0.4),
              borderRadius: BorderRadius.circular(10.0),
              border: Border.all(color: Colors.white.withOpacity(0.08)),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: const [
                      Text(
                        'Permit creators to reuse content?',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 12,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      SizedBox(height: 2),
                      Text(
                        'Grants other Localiiiy creators license to remix, react, or showcase while attributing you.',
                        style: TextStyle(
                          color: Color(0xFFCBD5E1),
                          fontSize: 10,
                          height: 1.3,
                        ),
                      ),
                    ],
                  ),
                ),
                Switch(
                  value: _config.permitReuse,
                  activeColor: const Color(0xFF10B981),
                  activeTrackColor: const Color(0xFF065F46),
                  inactiveThumbColor: Colors.grey,
                  inactiveTrackColor: Colors.black54,
                  onChanged: (bool val) {
                    _updateConfig(_config.copyWith(
                      permitReuse: val,
                      allowAudioReuse: val,
                      allowVideoRemapping: val,
                      allowMarketplaceShowcase: val,
                    ));
                  },
                ),
              ],
            ),
          ),

          // Granular Options (Animated Visibility when ON)
          AnimatedCrossFade(
            duration: const Duration(milliseconds: 250),
            crossFadeState: isLcc
                ? CrossFadeState.showFirst
                : CrossFadeState.showSecond,
            firstChild: Padding(
              padding: const EdgeInsets.top(10.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'GRANULAR PERMISSION PRESETS:',
                    style: TextStyle(
                      color: Color(0xFF34D399),
                      fontSize: 9.5,
                      fontWeight: FontWeight.bold,
                      fontFamily: 'monospace',
                    ),
                  ),
                  const SizedBox(height: 6),
                  _buildGranularCheckbox(
                    title: 'Audio / Sound Reuse Allowed',
                    subtitle: 'Permits audio extraction for Clips/Reels.',
                    icon: Icons.music_note,
                    value: _config.allowAudioReuse,
                    onChanged: (v) => _updateConfig(
                        _config.copyWith(allowAudioReuse: v ?? false)),
                  ),
                  _buildGranularCheckbox(
                    title: 'Video Remapping / Reaction Allowed',
                    subtitle: 'Permits split-screen/React usage.',
                    icon: Icons.video_camera_front,
                    value: _config.allowVideoRemapping,
                    onChanged: (v) => _updateConfig(
                        _config.copyWith(allowVideoRemapping: v ?? false)),
                  ),
                  _buildGranularCheckbox(
                    title: 'Marketplace Showcase Allowed',
                    subtitle: 'Permits cross-listing into community catalogs.',
                    icon: Icons.storefront,
                    value: _config.allowMarketplaceShowcase,
                    onChanged: (v) => _updateConfig(
                        _config.copyWith(allowMarketplaceShowcase: v ?? false)),
                  ),
                  const SizedBox(height: 6),
                  // Exemption Notice
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 8.0, vertical: 6.0),
                    decoration: BoxDecoration(
                      color: const Color(0xFF022C22),
                      borderRadius: BorderRadius.circular(8.0),
                      border: Border.all(
                          color: const Color(0xFF059669).withOpacity(0.5)),
                    ),
                    child: Row(
                      children: const [
                        Icon(Icons.shield, color: Color(0xFF10B981), size: 14),
                        SizedBox(width: 6),
                        Expanded(
                          child: Text(
                            'LCC Exemption: Permitted reuse is protected against automatic strikes.',
                            style: TextStyle(
                              color: Color(0xFFA7F3D0),
                              fontSize: 9.5,
                              height: 1.25,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            secondChild: const SizedBox.shrink(),
          ),
        ],
      ),
    );
  }

  Widget _buildGranularCheckbox({
    required String title,
    required String subtitle,
    required IconData icon,
    required bool value,
    required ValueChanged<bool?> onChanged,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 6.0),
      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 6.0),
      decoration: BoxDecoration(
        color: const Color(0xFF062016),
        borderRadius: BorderRadius.circular(8.0),
        border: Border.all(
            color: const Color(0xFF10B981).withOpacity(0.3), width: 0.5),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              Icon(icon,
                  size: 16,
                  color: value ? const Color(0xFF34D399) : Colors.grey),
              const SizedBox(width: 8),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      color: value ? Colors.white : Colors.grey,
                      fontSize: 11,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: const TextStyle(
                      color: Color(0xFF94A3B8),
                      fontSize: 9,
                    ),
                  ),
                ],
              ),
            ],
          ),
          Checkbox(
            value: value,
            activeColor: const Color(0xFF10B981),
            checkColor: Colors.black,
            onChanged: onChanged,
          ),
        ],
      ),
    );
  }
}
